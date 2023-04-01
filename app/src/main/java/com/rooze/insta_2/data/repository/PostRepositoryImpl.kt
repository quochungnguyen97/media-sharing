package com.rooze.insta_2.data.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.rooze.insta_2.data.remote.DataConstants
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.Comment
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.repository.PostRepository
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "PostRepositoryImpl"

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PostRepository {
    override suspend fun getPostsWithComment(
        startTime: Long,
        postsLimit: Int,
        commentsLimit: Int
    ): DataResult<List<PostComments>> = withContext(Dispatchers.IO) {
        val posts: List<Post> = suspendCoroutine { continuation ->
            firestore.collection(DataConstants.POST_COLLECTION)
                .whereLessThanOrEqualTo("time", startTime)
                .limit(postsLimit.toLong())
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { docs ->
                    continuation.resume(docs.map { doc -> doc.toPost() })
                }.addOnFailureListener { e ->
                    Log.e(TAG, "getPostsWithComment: ", e)
                    continuation.resume(null)
                }
        } ?: return@withContext DataResult.Fail("Failed to load posts")

        if (posts.isEmpty()) {
            return@withContext DataResult.Success(emptyList())
        }

        val postCommentsDeferred = posts.map { post ->
            async {
                PostComments(post, firestore.loadComments(post.id) ?: emptyList(), false)
            }
        }

        val accountsMapDeferred = async {
            firestore.getAccountMapByIds(posts.map { it.owner.id }.distinct())
        }

        val postsComments = postCommentsDeferred.awaitAll()
        val accountsMap = accountsMapDeferred.await()

        DataResult.Success(postsComments.map {
            it.copy(
                post = it.post.copy(owner = accountsMap[it.post.owner.id] ?: it.post.owner),
            )
        })
    }

    override suspend fun getPostsByAccount(accountId: String): DataResult<List<Post>> {
        val posts: List<Post> = suspendCoroutine { continuation ->
            firestore.collection(DataConstants.POST_COLLECTION)
                .whereEqualTo("owner", accountId)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { docs ->
                    continuation.resume(docs.map { doc ->
                        doc.toPost()
                    })
                }.addOnFailureListener { e ->
                    Log.e(TAG, "getPostsByAccount: ", e)
                    continuation.resume(null)
                }
        } ?: return DataResult.Fail("Failed to load posts")

        if (posts.isEmpty()) {
            return DataResult.Success(emptyList())
        }

        val owner = firestore.getAccountMapByIds(listOf(accountId))[accountId]
            ?: Account(id = accountId)

        return DataResult.Success(posts.map { it.copy(owner = owner) })
    }

    override suspend fun getPostById(id: String): DataResult<Post> {
        val post: Post = suspendCoroutine { continuation ->
            firestore.collection(DataConstants.POST_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener { doc ->
                    continuation.resume(doc.toPost())
                }.addOnFailureListener { e ->
                    Log.e(TAG, "getPostsByAccount: ", e)
                    continuation.resume(null)
                }
        } ?: return DataResult.Fail("Failed to load posts")

        val accountId = post.owner.id
        val account = firestore.getAccountMapByIds(listOf(accountId))[accountId]

        return DataResult.Success(post.copy(owner = account ?: post.owner))
    }

    override suspend fun getComments(postId: String): DataResult<List<Comment>> {
        val comments: List<Comment> =
            firestore.loadComments(postId) ?: return DataResult.Fail("Failed to get comments")

        val accountMap = firestore.getAccountMapByIds(comments.map { it.owner.id }.distinct())

        return DataResult.Success(comments.map {
            it.copy(
                owner = accountMap[it.owner.id] ?: it.owner
            )
        })
    }

    override suspend fun like(accountId: String, postId: String): DataResult<Post> {
        val post: Post = getPostById(postId).let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        } ?: return DataResult.Fail("Post data error")

        val likedAccountIds = post.likes.map { it.id }
        val result = if (likedAccountIds.contains(accountId)) {
            firestore.updateLikes(postId, likedAccountIds.filter { it != accountId })
        } else {
            firestore.updateLikes(postId, ArrayList<String>().apply {
                addAll(likedAccountIds)
                add(accountId)
            })
        }

        return if (result == null) {
            DataResult.Fail("Failed")
        } else {
            DataResult.Success(post.copy(likes = result.map { Account(id = it) }))
        }
    }

    override suspend fun comment(
        accountId: String,
        postId: String,
        content: String
    ): DataResult<Comment> {
        val comment: Comment = suspendCoroutine { continuation ->
            val time = System.currentTimeMillis()
            firestore.collection(DataConstants.COMMENT_COLLECTION)
                .add(
                    hashMapOf(
                        "owner" to accountId,
                        "post" to postId,
                        "content" to content,
                        "time" to time
                    )
                ).addOnSuccessListener { result ->
                    continuation.resume(
                        Comment(
                            id = result.id,
                            postId = postId,
                            owner = Account(accountId),
                            content = content,
                            time = time
                        )
                    )
                }.addOnFailureListener { e ->
                    Log.e(TAG, "comment: ", e)
                    continuation.resume(null)
                }
        } ?: return DataResult.Fail("Failed to comment")

        val account = firestore.getAccountMapByIds(listOf(accountId))[accountId] ?: comment.owner

        return DataResult.Success(comment.copy(owner = account))
    }

    override suspend fun delete(postId: String): DataResult<Boolean> {
        return suspendCoroutine { continuation ->
            firestore.collection(DataConstants.POST_COLLECTION)
                .document(postId)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(DataResult.Success(true))
                    } else {
                        Log.e(TAG, "delete: ", it.exception)
                        continuation.resume(DataResult.Fail("Delete failed"))
                    }
                }
        }
    }

    override suspend fun post(post: Post): DataResult<Post> {
        val result: Post = suspendCoroutine { continuation ->
            firestore.collection(DataConstants.POST_COLLECTION)
                .add(
                    hashMapOf(
                        "content" to post.content,
                        "imageUrl" to post.imageUrl,
                        "owner" to post.owner.id,
                        "time" to post.time
                    )
                ).addOnSuccessListener { ref ->
                    continuation.resume(post.copy(id = ref.id))
                }.addOnFailureListener { e ->
                    Log.e(TAG, "post: ", e)
                    continuation.resume(null)
                }
        } ?: return DataResult.Fail("Failed to post")

        return DataResult.Success(result)
    }
}

fun QueryDocumentSnapshot.toPost(): Post {
    val likes = (data["likes"] as? List<String>)?.let { listIds ->
        listIds.map { id -> Account(id = id) }
    } ?: emptyList()

    return Post(
        id = id,
        content = data["content"]?.toString() ?: "",
        imageUrl = data["imageUrl"]?.toString() ?: "",
        time = data["time"]?.toString()?.toLong() ?: 0L,
        owner = Account(id = data["owner"]?.toString() ?: ""),
        likes = likes
    )
}

fun DocumentSnapshot.toPost(): Post {
    return Post(
        id = id,
        content = data?.get("content")?.toString() ?: "",
        imageUrl = data?.get("imageUrl")?.toString() ?: "",
        time = data?.get("time")?.toString()?.toLong() ?: 0L,
        owner = Account(id = data?.get("owner")?.toString() ?: ""),
        likes = (data?.get("likes") as? List<String>)?.let { listIds ->
            listIds.map { Account(id = it) }
        } ?: emptyList()
    )
}

fun QueryDocumentSnapshot.toComment(): Comment {
    return Comment(
        id = id,
        postId = data["post"]?.toString() ?: "",
        owner = Account(data["owner"]?.toString() ?: ""),
        content = data["content"]?.toString() ?: "",
        time = data["time"]?.toString()?.toLong() ?: -1
    )
}

suspend fun FirebaseFirestore.getAccountMapByIds(
    accountIds: List<String>
): Map<String, Account> = withContext(Dispatchers.Default) {

    if (accountIds.isEmpty()) {
        return@withContext emptyMap()
    }

    getAccountsListByIds(accountIds).associateBy { account -> account.id }
}

suspend fun FirebaseFirestore.updateLikes(
    postId: String,
    likes: List<String>
): List<String>? = suspendCoroutine { continuation ->
    collection(DataConstants.POST_COLLECTION)
        .document(postId)
        .update("likes", likes)
        .addOnCompleteListener {
            continuation.resume(if (it.isSuccessful) likes else null)
        }
}

suspend fun FirebaseFirestore.loadComments(
    postId: String
): List<Comment>? = suspendCoroutine { continuation ->
    collection(DataConstants.COMMENT_COLLECTION)
        .whereEqualTo("post", postId)
        .get()
        .addOnSuccessListener { docs ->
            continuation.resume(docs.map { it.toComment() })
        }.addOnFailureListener { e ->
            Log.e(TAG, "getComments: ", e)
            continuation.resume(null)
        }
}
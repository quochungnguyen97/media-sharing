package com.rooze.insta_2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.rooze.insta_2.data.remote.DataConstants
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Comment
import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.repository.NotificationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "NotificationRepositoryI"

class NotificationRepositoryImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val firestore: FirebaseFirestore
) : NotificationRepository {
    override suspend fun addNotification(notification: Notification): DataResult<Notification> {
        return suspendCoroutine { continuation ->
            firebaseDatabase.reference.child(DataConstants.NOTIFICATION_COLLECTION)
                .child(notification.receivingAccountId)
                .child(notification.toChildPath())
                .setValue(notification.toMap())
                .addOnCompleteListener { task ->
                    Log.i(TAG, "addNotification: ${task.isSuccessful} ${task.exception}")
                    if (task.isSuccessful) {
                        continuation.resume(DataResult.Success(notification))
                    } else {
                        continuation.resume(DataResult.Fail("Failed to add", task.exception))
                    }
                }
        }
    }

    override suspend fun deleteNotification(notification: Notification): DataResult<Boolean> {
        return suspendCoroutine { continuation ->
            firebaseDatabase.reference.child(DataConstants.NOTIFICATION_COLLECTION)
                .child(notification.receivingAccountId)
                .child(notification.toChildPath())
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(DataResult.Success(true))
                    } else {
                        continuation.resume(DataResult.Fail("Failed to remove", task.exception))
                    }
                }

        }
    }

    override suspend fun startListenNotification(
        accountId: String
    ): Flow<List<Notification>> = callbackFlow {
        val reference = firebaseDatabase.reference.child(DataConstants.NOTIFICATION_COLLECTION)
            .child(accountId)

        reference.get().addOnSuccessListener { snapshot ->
            launch {
                trySend(snapshot.toNotificationList(firestore))
            }
        }

        val listener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    trySend(snapshot.toNotificationList(firestore))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cancel()
            }
        })

        awaitClose {
            reference.removeEventListener(listener)
        }
    }
}

private suspend fun DataSnapshot.toNotificationList(firestore: FirebaseFirestore): List<Notification> {
    val commentNotifications = child(DataConstants.COMMENT_NOTIFICATION_CHILD)
        .children
        .mapNotNull {
            val map = it.value as? Map<String, Any?>

            if (map != null && it.key != null && !it.key.isNullOrEmpty()) {
                map.toNotification(commentId = it.key)
            } else {
                null
            }
        }.filterIsInstance<Notification.Comment>()

    val comments = firestore.getCommentsByIds(commentNotifications.map { it.commentId })

    val likeNotifications = child(DataConstants.LIKE_NOTIFICATION_CHILD)
        .children
        .mapNotNull {
            val map = it.value as? Map<String, Any?>

            if (map != null && it.key != null && !it.key.isNullOrEmpty()) {
                map.toNotification(postId = it.key)
            } else {
                null
            }
        }.filterIsInstance<Notification.Like>()

    Log.i(TAG, "toNotificationList: ${comments.values} ${likeNotifications.map { it.postId }}")

    val posts = firestore.getPostsByIds(ArrayList<String>().apply {
        addAll(comments.values.map { it.postId })
        addAll(likeNotifications.map { it.postId })
    }.distinct())

    val accounts = firestore.getAccountMapByIds(ArrayList<String>().apply {
        addAll(likeNotifications.map { it.likerId })
        addAll(comments.values.map { it.owner.id })
    })

    return ArrayList<Notification>().apply {
        addAll(commentNotifications.map {
            it.comment = comments[it.commentId]?.let { comment ->
                comment.copy(owner = accounts[comment.owner.id] ?: comment.owner)
            }
            it.post = posts[it.comment?.postId]
            it
        })
        addAll(likeNotifications.map {
            it.post = posts[it.postId]
            it.liker = accounts[it.likerId]
            it
        })
    }
}

private suspend fun FirebaseFirestore.getCommentsByIds(
    commentIds: List<String>
): Map<String, Comment> = withContext(Dispatchers.IO) {
    if (commentIds.isEmpty()) {
        return@withContext HashMap()
    }

    commentIds.chunked(10)
        .map { ids ->
            async<List<Comment>> {
                suspendCoroutine { continuation ->
                    collection(DataConstants.COMMENT_COLLECTION)
                        .whereIn(FieldPath.documentId(), ids)
                        .get()
                        .addOnSuccessListener {  snapshot ->
                            continuation.resume(snapshot.map { it.toComment() })
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "getCommentsByIds: ", e)
                            continuation.resume(emptyList())
                        }
                }
            }
        }.awaitAll().fold(hashMapOf()) { map, comments ->
            Log.i(TAG, "getCommentsByIds: $comments")
            for (comment in comments) {
                map[comment.id] = comment
            }
            map
        }
}

private suspend fun FirebaseFirestore.getPostsByIds(
    postIds: List<String>
): Map<String, Post> = withContext(Dispatchers.IO) {
    if (postIds.isEmpty()) {
        return@withContext HashMap()
    }

    postIds.chunked(10)
        .map { ids ->
            async<List<Post>> {
                suspendCoroutine { continuation ->
                    collection(DataConstants.POST_COLLECTION)
                        .whereIn(FieldPath.documentId(), ids)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            continuation.resume(snapshot.map { it.toPost() })
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "getPostsByIds: ", e)
                            continuation.resume(emptyList())
                        }
                }
            }
        }.awaitAll().fold(hashMapOf()) { map, posts ->
            for (post in posts) {
                map[post.id] = post
            }
            map
        }
}

private fun Notification.toMap(): Map<String, String> {
    return when (this) {
        is Notification.Comment -> mapOf(
            "dummy" to "dummy"
        )
        is Notification.Like -> mapOf(
            "liker" to likerId
        )
        else -> throw IllegalArgumentException("Invalid notification type $this")
    }
}

private fun Notification.toChildPath(): String {
    return when (this) {
        is Notification.Comment -> "${DataConstants.COMMENT_NOTIFICATION_CHILD}/$commentId"
        is Notification.Like -> "${DataConstants.LIKE_NOTIFICATION_CHILD}/$postId"
        else -> throw IllegalArgumentException("Invalid notification type $this")
    }
}

private fun Map<String, Any?>.toNotification(
    commentId: String? = null,
    postId: String? = null
): Notification? {
    if (commentId != null) {
        return Notification.Comment("", commentId)
    }

    if (postId != null) {
        return Notification.Like("", postId, get("liker") as String)
    }

    return null
}

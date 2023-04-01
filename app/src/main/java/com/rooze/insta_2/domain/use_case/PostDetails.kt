package com.rooze.insta_2.domain.use_case

import android.util.Log
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class PostDetails(
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository,
    private val imageRepository: ImageRepository
) {

    companion object {
        private const val TAG = "PostDetails"
    }

    private var postComments: PostComments = PostComments()

    suspend fun getPostDetails(
        postId: String
    ): DataResult<PostComments> = withContext(Dispatchers.IO) {
        val accountId = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        }

        val postDeferred = async {
            postRepository.getPostById(postId).let {
                when (it) {
                    is DataResult.Fail -> null
                    is DataResult.Success -> it.data
                }
            }
        }

        val commentsDeferred = async {
            postRepository.getComments(postId).let {
                when (it) {
                    is DataResult.Fail -> null
                    is DataResult.Success -> it.data
                }
            }
        }

        val post =
            postDeferred.await() ?: return@withContext DataResult.Fail("Failed to get post data")

        val comments = commentsDeferred.await() ?: emptyList()

        postComments = PostComments(
            post,
            comments,
            post.likes.map { it.id }.contains(accountId)
        )

        Log.i(TAG, "getPostDetails: $postComments")

        DataResult.Success(postComments)
    }

    suspend fun like(): DataResult<PostComments> {
        val accountId = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        } ?: return DataResult.Fail("Login required")

        val likeResult = postRepository.like(accountId, postComments.post.id)

        if (likeResult is DataResult.Fail) {
            return DataResult.Fail(likeResult.message)
        }

        val likedPost = (likeResult as DataResult.Success).data

        postComments = postComments.copy(
            post = postComments.post.copy(likes = likedPost.likes),
            liked = likedPost.likes.map { it.id }.contains(accountId)
        )

        return DataResult.Success(postComments)
    }

    suspend fun comment(content: String): DataResult<PostComments> {
        val accountId = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        } ?: return DataResult.Fail("Login required")

        val commentResult = postRepository.comment(accountId, postComments.post.id, content)

        if (commentResult is DataResult.Fail) {
            return DataResult.Fail(commentResult.message)
        }

        val comment = (commentResult as DataResult.Success).data

        postComments = postComments.copy(comments = postComments.comments.toMutableList().apply {
            add(comment)
        })

        return DataResult.Success(postComments)
    }

    suspend fun delete(): DataResult<Boolean> {
        val accountId = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        } ?: return DataResult.Fail("Login required")

        if (postComments.post.owner.id != accountId) {
            return DataResult.Fail("Authenticated as fail")
        }

        val postId = postComments.post.id
        if (postId.isEmpty()) {
            return DataResult.Fail("Post id error")
        }

        return postRepository.delete(postId).let {
            if (it is DataResult.Success && postComments.post.imageUrl.isNotEmpty()) {
                imageRepository.deleteImage(postComments.post.imageUrl)
            }
            it
        }
    }
}
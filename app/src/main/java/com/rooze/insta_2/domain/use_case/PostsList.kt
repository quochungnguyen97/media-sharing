package com.rooze.insta_2.domain.use_case

import android.util.Log
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostsList(
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) {
    companion object {
        private const val POSTS_PER_LOAD = 100
        private const val TAG = "PostsList"
    }

    private var posts: MutableList<PostComments> = arrayListOf()

    suspend fun loadMorePosts(): DataResult<List<PostComments>> = withContext(Dispatchers.Default) {
        val startTime = if (posts.isEmpty()) {
            System.currentTimeMillis()
        } else {
            posts.last().post.time
        }

        val postsResult = postRepository.getPostsWithComment(
            startTime = startTime,
            postsLimit = POSTS_PER_LOAD,
            commentsLimit = 2
        )

        if (postsResult is DataResult.Fail) {
            return@withContext DataResult.Fail(postsResult.message)
        }

        val accountId = accountRepository.getCurrentAccountId().let {
            when (it) {
                is DataResult.Fail -> null
                is DataResult.Success -> it.data
            }
        }

        val loadedPosts = (postsResult as DataResult.Success).data.map { post ->
            if (accountId != null) {
                post.copy(
                    liked = post.post.likes.map { likedAccount ->
                        likedAccount.id
                    }.contains(accountId)
                )
            } else {
                post
            }
        }

        Log.i(TAG, "loadMorePosts: $loadedPosts")

        if (loadedPosts.isEmpty()) {
            return@withContext DataResult.Fail("No more post")
        }

        val mergedList = ArrayList<PostComments>().apply {
            addAll(posts)
            addAll(loadedPosts.sortedByDescending { it.post.time })
        }

        Log.i(TAG, "loadMorePosts: $posts $mergedList ${posts != mergedList}")
        if (posts != mergedList) {
            posts = ArrayList(mergedList.distinctBy { it.post.id })
        }

        DataResult.Success(posts)
    }

    suspend fun like(postId: String): DataResult<List<PostComments>> =
        withContext(Dispatchers.Default) {
            val accountId = accountRepository.getCurrentAccountId().let {
                when (it) {
                    is DataResult.Fail -> null
                    is DataResult.Success -> it.data
                }
            } ?: return@withContext DataResult.Fail("Login required")

            val likeResult = postRepository.like(accountId, postId)

            if (likeResult is DataResult.Fail) {
                return@withContext DataResult.Fail(likeResult.message)
            }

            val newPost = (likeResult as DataResult.Success).data

            Log.i(TAG, "like: $newPost")

            val newPosts = posts.map { post ->
                if (post.post.id == newPost.id) {
                    post.copy(
                        post = post.post.copy(likes = newPost.likes),
                        liked = newPost.likes.map { it.id }.contains(accountId)
                    )
                } else {
                    post
                }
            }

            posts = ArrayList(newPosts)

            DataResult.Success(posts)
        }

    suspend fun reloadPostsList(): DataResult<List<PostComments>> {
        posts.clear()
        return loadMorePosts()
    }
}
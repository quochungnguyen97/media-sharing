package com.rooze.insta_2.domain.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Comment
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.entity.PostComments

interface PostRepository {
    suspend fun getPostsWithComment(
        startTime: Long = System.currentTimeMillis(),
        postsLimit: Int = 1000,
        commentsLimit: Int = 20
    ): DataResult<List<PostComments>>

    suspend fun getPostsByAccount(accountId: String): DataResult<List<Post>>

    suspend fun getPostById(id: String): DataResult<Post>

    suspend fun getComments(postId: String): DataResult<List<Comment>>

    suspend fun like(accountId: String, postId: String): DataResult<Post>

    suspend fun comment(accountId: String, postId: String, content: String): DataResult<Comment>

    suspend fun delete(postId: String): DataResult<Boolean>

    suspend fun post(post: Post): DataResult<Post>
}
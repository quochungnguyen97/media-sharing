package com.rooze.insta_2.domain.use_case.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Comment
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.repository.PostRepository

class MockPostRepository(var isSuccess: Boolean = true) : PostRepository {
    override suspend fun getPostsWithComment(
        startTime: Long,
        postsLimit: Int,
        commentsLimit: Int
    ): DataResult<List<PostComments>> {
        return if (isSuccess) {
            DataResult.Success(listOf(PostComments(Post(), listOf(), false)))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getPostsByAccount(accountId: String): DataResult<List<Post>> {
        return if (isSuccess) {
            DataResult.Success(listOf(Post()))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getPostById(id: String): DataResult<Post> {
        return if (isSuccess) {
            DataResult.Success(Post())
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getComments(postId: String): DataResult<List<Comment>> {
        return if (isSuccess) {
            DataResult.Success(listOf(Comment()))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun like(accountId: String, postId: String): DataResult<Post> {
        return if (isSuccess) {
            DataResult.Success(Post())
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun comment(
        accountId: String,
        postId: String,
        content: String
    ): DataResult<Comment> {
        return if (isSuccess) {
            DataResult.Success(Comment())
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun delete(postId: String): DataResult<Boolean> {
        return if (isSuccess) {
            DataResult.Success(true)
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun post(post: Post): DataResult<Post> {
        return if (isSuccess) {
            DataResult.Success(Post())
        } else {
            DataResult.Fail("Failed")
        }
    }
}
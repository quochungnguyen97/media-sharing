package com.rooze.insta_2.domain.use_case

import android.util.Log
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.PostRepository

class LikesList(
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) {
    companion object {
        private const val TAG = "LikesList"
    }

    suspend fun getLikesList(postId: String): DataResult<List<Account>> {
        val post = postRepository.getPostById(postId).successDataOrNull()
            ?: return DataResult.Fail("Error when getting post data")

        Log.i(TAG, "getLikesList: $post")
        return accountRepository.getAccountsByIds(post.likes.map { it.id })
    }
}
package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.PostRepository

class ProfileInfo(
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository
) {
    suspend fun getProfilePosts(accountId: String): DataResult<List<Post>> {
        return when (val dataResult = postRepository.getPostsByAccount(accountId)) {
            is DataResult.Fail -> dataResult
            is DataResult.Success -> DataResult.Success(dataResult.data.sortedByDescending { it.time })
        }
    }

    suspend fun getAccountInfo(accountId: String): DataResult<Account> {
        return accountRepository.getAccountById(accountId)
    }
}
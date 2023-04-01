package com.rooze.insta_2.domain.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account

interface AccountRepository {
    suspend fun getCurrentAccount(): DataResult<Account>
    suspend fun getCurrentAccountId(): DataResult<String>
    suspend fun getAccountById(accountId: String): DataResult<Account>
    suspend fun getAccountsByIds(ids: List<String>): DataResult<List<Account>>
    suspend fun login(email: String, password: String): DataResult<Account>
    suspend fun oneTapLogin(tokenId: String): DataResult<Account>
    suspend fun register(account: Account, password: String): DataResult<Account>
    suspend fun updateAccountInfo(avatarUrl: String? = null, name: String? = null): DataResult<Account>
    suspend fun logout()
}
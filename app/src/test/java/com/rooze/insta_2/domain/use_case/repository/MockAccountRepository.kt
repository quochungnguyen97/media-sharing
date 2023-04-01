package com.rooze.insta_2.domain.use_case.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.repository.AccountRepository

class MockAccountRepository(var success: Boolean = true) : AccountRepository {
    override suspend fun getCurrentAccount(): DataResult<Account> {
        return if (success) {
            DataResult.Success(Account())
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getCurrentAccountId(): DataResult<String> {
        return if (success) {
            DataResult.Success("1")
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getAccountById(accountId: String): DataResult<Account> {
        return if (success) {
            DataResult.Success(Account(id = accountId))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun getAccountsByIds(ids: List<String>): DataResult<List<Account>> {
        return if (success) {
            DataResult.Success(ids.map { Account(id = it) })
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun login(email: String, password: String): DataResult<Account> {
        return if (success) {
            DataResult.Success(Account(
                email = email
            ))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun oneTapLogin(tokenId: String): DataResult<Account> {
        return if (success) {
            DataResult.Success(Account())
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun register(account: Account, password: String): DataResult<Account> {
        return if (success) {
            DataResult.Success(account)
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun updateAccountInfo(avatarUrl: String?, name: String?): DataResult<Account> {
        return if (success) {
            DataResult.Success(Account(
                name = name ?: "",
                avatarUrl = avatarUrl ?: ""
            ))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun logout() {
    }
}
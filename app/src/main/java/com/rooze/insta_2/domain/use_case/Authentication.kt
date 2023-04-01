package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.repository.ImageRepository
import java.io.InputStream

class Authentication(
    private val accountRepository: AccountRepository,
    private val imageRepository: ImageRepository
) {
    suspend fun getCurrentAccount(): DataResult<Account> {
        return accountRepository.getCurrentAccount()
    }

    suspend fun getCurrentAccountId(): DataResult<String> {
        return accountRepository.getCurrentAccountId()
    }

    suspend fun login(email: String, password: String): DataResult<Account> {
        if (email.isEmpty()) {
            return DataResult.Fail("Email must not be empty")
        }
        if (password.isEmpty()) {
            return DataResult.Fail("Password must not be empty")
        }

        return accountRepository.login(email, password)
    }

    suspend fun oneTapLogin(tokenId: String): DataResult<Account> {
        return if (tokenId.isEmpty()) {
            DataResult.Fail("Invalid token id")
        } else {
            accountRepository.oneTapLogin(tokenId)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        password2: String,
        avatarInputStream: InputStream?
    ): DataResult<Account> {
        if (email.isEmpty()) {
            return DataResult.Fail("Email must not be empty")
        }
        if (password.isEmpty()) {
            return DataResult.Fail("Password must not be empty")
        }
        if (password != password2) {
            return DataResult.Fail("2 passwords must be same")
        }
        if (name.isEmpty()) {
            return DataResult.Fail("Display name must not be empty")
        }

        val createAccountResult =  accountRepository.register(
            Account(name = name, email = email),
            password
        )

        if (createAccountResult is DataResult.Fail) {
            return createAccountResult
        }

        if (avatarInputStream != null) {
            val imageUrlResult = imageRepository.uploadImage(avatarInputStream)
            if (imageUrlResult is DataResult.Fail) {
                return createAccountResult
            }

            val imageUrl = (imageUrlResult as DataResult.Success).data

            return accountRepository.updateAccountInfo(avatarUrl = imageUrl)
        }

        return createAccountResult
    }

    suspend fun logout() {
        accountRepository.logout()
    }
}
package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.use_case.repository.MockAccountRepository
import com.rooze.insta_2.domain.use_case.repository.MockImageRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.InputStream

class AuthenticationTest {

    @Test
    fun `login test empty email`() = runBlocking {
        val accountRepository = MockAccountRepository(true)
        val imageRepository = MockImageRepository(true)

        val authentication = Authentication(accountRepository, imageRepository)

        val loginResult = authentication.login("", "123")

        assertTrue(loginResult is DataResult.Fail)
        assertEquals("Email must not be empty", (loginResult as DataResult.Fail).message)
    }

    @Test
    fun `login test empty password`() = runBlocking {
        val accountRepository = MockAccountRepository(true)
        val imageRepository = MockImageRepository(true)

        val authentication = Authentication(accountRepository, imageRepository)

        val loginResult = authentication.login("hung@gmail.com", "")

        assertTrue(loginResult is DataResult.Fail)
        assertEquals("Password must not be empty", (loginResult as DataResult.Fail).message)
    }

    @Test
    fun `login test success case`() = runBlocking {
        val accountRepository = MockAccountRepository(true)
        val imageRepository = MockImageRepository(true)

        val authentication = Authentication(accountRepository, imageRepository)

        val loginResult = authentication.login("hung@gmail.com", "123")

        assertTrue(loginResult is DataResult.Success)
    }

    @Test
    fun `login test fail case`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = false,
            successImageRepository = true
        )

        val loginResult = authentication.login("hung@gmail.com", "123")

        assertTrue(loginResult is DataResult.Fail)
        assertEquals("Failed", (loginResult as DataResult.Fail).message)
    }

    @Test
    fun `getCurrentAccount test success case`() = runBlocking {
        val authentication =
            provideAuthentication(successAccountRepository = true, successImageRepository = true)

        val currentAccountResult = authentication.getCurrentAccount()

        assertTrue(currentAccountResult is DataResult.Success)
    }

    @Test
    fun `getCurrentAccount test fail case`() = runBlocking {
        val authentication =
            provideAuthentication(successAccountRepository = false, successImageRepository = true)

        val currentAccountResult = authentication.getCurrentAccount()

        assertTrue(currentAccountResult is DataResult.Fail)
    }

    @Test
    fun `register test empty cases`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = true,
            successImageRepository = true
        )

        val emptyEmailResult = authentication.register(
            "Hung",
            "",
            "123",
            "123",
            null
        )
        assertTrue(emptyEmailResult is DataResult.Fail)
        assertEquals("Email must not be empty", (emptyEmailResult as DataResult.Fail).message)

        val emptyPasswordResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "",
            "123",
            null
        )
        assertTrue(emptyPasswordResult is DataResult.Fail)
        assertEquals("Password must not be empty", (emptyPasswordResult as DataResult.Fail).message)

        val passwordNotMatchResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "123",
            "345",
            null
        )
        assertTrue(passwordNotMatchResult is DataResult.Fail)
        assertEquals("2 passwords must be same", (passwordNotMatchResult as DataResult.Fail).message)
    }

    @Test
    fun `register fail to register case`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = false,
            successImageRepository = true
        )

        val registerResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "123",
            "123",
            null
        )
        assertTrue(registerResult is DataResult.Fail)
        assertEquals("Failed", (registerResult as DataResult.Fail).message)
    }

    @Test
    fun `register success to register without image case`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = true,
            successImageRepository = true
        )

        val registerResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "123",
            "123",
            null
        )
        assertTrue(registerResult is DataResult.Success)
        val account = (registerResult as DataResult.Success).data
        assertEquals("", account.avatarUrl)
        assertEquals("Hung", account.name)
        assertEquals("hung@gmail.com", account.email)

    }

    @Test
    fun `register success to register with image case`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = true,
            successImageRepository = true
        )

        val registerResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "123",
            "123",
            InputStream.nullInputStream()
        )
        assertTrue(registerResult is DataResult.Success)
        assertEquals("image_data", (registerResult as DataResult.Success).data.avatarUrl)
    }

    @Test
    fun `register success to register with image failed image upload case`() = runBlocking {
        val authentication = provideAuthentication(
            successAccountRepository = true,
            successImageRepository = false
        )

        val registerResult = authentication.register(
            "Hung",
            "hung@gmail.com",
            "123",
            "123",
            InputStream.nullInputStream()
        )
        assertTrue(registerResult is DataResult.Success)
        assertEquals("", (registerResult as DataResult.Success).data.avatarUrl)
    }

    private fun provideAuthentication(
        successAccountRepository: Boolean,
        successImageRepository: Boolean
    ): Authentication {
        val accountRepository = MockAccountRepository(successAccountRepository)
        val imageRepository = MockImageRepository(successImageRepository)
        return Authentication(accountRepository, imageRepository)
    }
}
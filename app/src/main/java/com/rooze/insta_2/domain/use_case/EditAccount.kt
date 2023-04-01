package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import java.io.InputStream

class EditAccount(
    private val accountRepository: AccountRepository,
    private val imageRepository: ImageRepository
) {
    suspend fun editAccountInfo(
        accountName: String? = null,
        avatarInputStream: InputStream? = null
    ): DataResult<Account> {
        val imageUrl = if (avatarInputStream != null) {
            imageRepository.uploadImage(avatarInputStream).let {
                when (it) {
                    is DataResult.Fail -> ""
                    is DataResult.Success -> it.data
                }
            }
        } else {
            ""
        }

        return if (imageUrl.isNotEmpty()) {
            val oldAvatarUrl = accountRepository.getCurrentAccount().let {
                when (it) {
                    is DataResult.Fail -> ""
                    is DataResult.Success -> it.data.avatarUrl
                }
            }
            accountRepository.updateAccountInfo(avatarUrl = imageUrl, name = accountName).let {
                if (it is DataResult.Success && oldAvatarUrl.isNotEmpty()) {
                    imageRepository.deleteImage(oldAvatarUrl)
                }
                it
            }
        } else if (accountName != null) {
            accountRepository.updateAccountInfo(name = accountName)
        } else {
            DataResult.Fail("No info to update")
        }
    }
}
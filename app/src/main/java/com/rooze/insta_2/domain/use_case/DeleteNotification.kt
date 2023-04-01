package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.NotificationRepository

class DeleteNotification(
    private val accountRepository: AccountRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification): DataResult<Boolean> {
        val accountId = accountRepository.getCurrentAccountId()
            .successDataOrNull() ?: return DataResult.Fail("Login required!")

        if (accountId != notification.receivingAccountId) {
            return DataResult.Fail("Invalid account")
        }

        return notificationRepository.deleteNotification(notification)
    }
}
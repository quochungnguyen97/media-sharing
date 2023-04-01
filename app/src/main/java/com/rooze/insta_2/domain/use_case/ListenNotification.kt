package com.rooze.insta_2.domain.use_case

import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ListenNotification(
    private val notificationRepository: NotificationRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Flow<List<Notification>> {
        val accountId = accountRepository.getCurrentAccountId().successDataOrNull()
            ?: return emptyFlow()
        return notificationRepository.startListenNotification(accountId)
    }
}
package com.rooze.insta_2.domain.use_case.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockNotificationRepository(var isSuccess: Boolean = true) : NotificationRepository {
    override suspend fun addNotification(notification: Notification): DataResult<Notification> {
        return if (isSuccess) {
            DataResult.Success(Notification.Comment("", ""))
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun deleteNotification(notification: Notification): DataResult<Boolean> {
        return if (isSuccess) {
            DataResult.Success(true)
        } else {
            DataResult.Fail("Failed")
        }
    }

    override suspend fun startListenNotification(accountId: String): Flow<List<Notification>> {
        return flowOf(listOf(Notification.Comment("", "")))
    }
}
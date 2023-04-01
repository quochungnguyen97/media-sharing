package com.rooze.insta_2.domain.repository

import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun addNotification(notification: Notification): DataResult<Notification>
    suspend fun deleteNotification(notification: Notification): DataResult<Boolean>
    suspend fun startListenNotification(accountId: String): Flow<List<Notification>>
}
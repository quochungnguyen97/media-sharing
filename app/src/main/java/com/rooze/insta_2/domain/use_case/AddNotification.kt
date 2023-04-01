package com.rooze.insta_2.domain.use_case

import android.util.Log
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Comment
import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.NotificationRepository

private const val TAG = "AddNotification"

class AddNotification(
    private val notificationRepository: NotificationRepository,
    private val accountRepository: AccountRepository
) {
    suspend fun postCommentNotification(
        comment: Comment,
        receivingAccountId: String
    ): DataResult<Notification> {
        val accountId = accountRepository.getCurrentAccountId().successDataOrNull()
            ?: return DataResult.Fail("Login required!")

        Log.i(TAG, "postCommentNotification: $accountId ${comment.owner.id}")
        if (accountId != comment.owner.id) {
            return DataResult.Fail("Invalid authentication")
        }

        if (accountId == receivingAccountId) {
            return DataResult.Fail("Self comment")
        }

        val notification =
            Notification.Comment(commentId = comment.id, receivingAccountId = receivingAccountId)
        return notificationRepository.addNotification(notification)
    }

    suspend fun postLikeNotification(post: Post): DataResult<Notification> {
        val accountId = accountRepository.getCurrentAccountId().successDataOrNull()
            ?: return DataResult.Fail("Login required!")

        if (!post.likes.map { it.id }.contains(accountId)) {
            return DataResult.Fail("Invalid authentication")
        }

        if (accountId == post.owner.id) {
            return DataResult.Fail("Self like")
        }

        return notificationRepository.addNotification(
            Notification.Like(
                postId = post.id,
                receivingAccountId = post.owner.id,
                likerId = accountId
            )
        )
    }
}
package com.rooze.insta_2.presentation.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.rooze.insta_2.domain.entity.Notification
import com.rooze.insta_2.domain.use_case.Authentication
import com.rooze.insta_2.domain.use_case.DeleteNotification
import com.rooze.insta_2.domain.use_case.ListenNotification
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.post_details.PostDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PostNotificationService : Service() {

    companion object {
        private const val TAG = "PostNotificationService"
    }

    @Inject
    lateinit var listenNotification: ListenNotification

    @Inject
    lateinit var deleteNotification: DeleteNotification

    @Inject
    lateinit var authentication: Authentication

    @Inject
    lateinit var mediaNotificationManager: MediaNotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var listeningJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate: ")
        startListeningNotifications()
    }

    private fun startListeningNotifications() {
        listeningJob?.cancel()
        listeningJob = serviceScope.launch {
            listenNotification().collect { notifications ->
                val notificationsCount = withContext(Dispatchers.IO) {
                    getSharedPreferences(
                        ViewConstants.POST_NOTIFICATION_PREFERENCE_NAME,
                        MODE_PRIVATE
                    ).getInt(ViewConstants.POST_NOTIFICATION_IDS_PREFERENCE_KEY, 0)
                }
                Log.i(TAG, "startListeningNotifications: $notificationsCount $notifications")
                repeat(notificationsCount) {
                    mediaNotificationManager.cancel(NotificationType.POST.getNotificationId(it))
                }

                notifications.forEachIndexed { index, notification ->
                    val message = when (notification) {
                        is Notification.Comment -> {
                            "${notification.comment?.owner?.name ?: "Someone"} has commented on your post${notification.post?.content?.let { " \"$it\"" } ?: ""}"
                        }
                        is Notification.Like -> {
                            "${notification.liker?.name ?: "Someone"} has liked your post${notification.post?.content?.let { " \"$it\"" } ?: ""}"
                        }
                        else -> throw IllegalArgumentException("Invalid notification type $notification")
                    }
                    mediaNotificationManager.makeNotification(
                        NotificationType.POST.getNotificationId(index),
                        message,
                        android.R.drawable.ic_menu_gallery,
                        NotificationCompat.PRIORITY_MIN,
                        PendingIntent.getService(
                            applicationContext,
                            index,
                            Intent(applicationContext, PostNotificationService::class.java).apply {
                                action = ViewConstants.ACTION_OPEN_FROM_NOTIFICATION
                                when (notification) {
                                    is Notification.Comment -> {
                                        putExtra(
                                            ViewConstants.EXTRA_COMMENT_NOTIFICATION_ID,
                                            notification.commentId
                                        )
                                        notification.post?.let {
                                            putExtra(
                                                ViewConstants.EXTRA_POST_ID,
                                                it.id
                                            )
                                        }
                                    }
                                    is Notification.Like -> {
                                        putExtra(
                                            ViewConstants.EXTRA_LIKE_NOTIFICATION_ID,
                                            notification.postId
                                        )
                                        notification.post?.let {
                                            putExtra(
                                                ViewConstants.EXTRA_POST_ID,
                                                it.id
                                            )
                                        }
                                    }
                                    else -> throw IllegalArgumentException("Invalid notification type $notification")
                                }
                            },
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    )
                }
                withContext(Dispatchers.IO) {
                    getSharedPreferences(ViewConstants.POST_NOTIFICATION_PREFERENCE_NAME, MODE_PRIVATE)
                        .edit {
                            putInt(
                                ViewConstants.POST_NOTIFICATION_IDS_PREFERENCE_KEY,
                                notifications.size
                            )
                        }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action ?: return START_STICKY

        Log.i(TAG, "onStartCommand: $action")

        when (action) {
            ViewConstants.ACTION_OPEN_FROM_NOTIFICATION -> {
                val commentNotificationId = intent.getStringExtra(
                    ViewConstants.EXTRA_COMMENT_NOTIFICATION_ID
                )

                if (!commentNotificationId.isNullOrEmpty()) {
                    serviceScope.launch {
                        val accountId = authentication.getCurrentAccountId().successDataOrNull()
                            ?: return@launch

                        deleteNotification(Notification.Comment(accountId, commentNotificationId))
                    }
                }

                val likeNotificationId = intent.getStringExtra(
                    ViewConstants.EXTRA_LIKE_NOTIFICATION_ID
                )

                if (!likeNotificationId.isNullOrEmpty()) {
                    serviceScope.launch {
                        val accountId = authentication.getCurrentAccountId().successDataOrNull()
                            ?: return@launch

                        deleteNotification(Notification.Like(accountId, likeNotificationId, ""))
                    }
                }

                val postId = intent.getStringExtra(ViewConstants.EXTRA_POST_ID)

                Log.i(TAG, "onStartCommand: $commentNotificationId $likeNotificationId $postId")

                if (!postId.isNullOrEmpty()) {
                    startActivity(Intent(
                        applicationContext,
                        PostDetailsActivity::class.java
                    ).apply {
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(ViewConstants.EXTRA_POST_ID, postId)
                    })
                }
            }
            ViewConstants.ACTION_REREGISTER_NOTIFICATION_LISTENER -> startListeningNotifications()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
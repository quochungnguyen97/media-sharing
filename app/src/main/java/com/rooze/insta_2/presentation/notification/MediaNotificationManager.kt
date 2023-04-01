package com.rooze.insta_2.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MediaNotificationManager(private val context: Context) {
    private val notificationManager: NotificationManager

    init {
        val channel = NotificationChannel(
            "STATUS",
            "Status Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Channel for post status notifications"

        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun makeNotification(
        notificationId: Int,
        message: String,
        icon: Int = android.R.drawable.ic_menu_gallery,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        pendingIntent: PendingIntent? = null
    ) {
        val notificationBuilder = NotificationCompat.Builder(context, "STATUS")
            .setSmallIcon(icon)
            .setContentTitle(message)
            .setPriority(priority)
            .setVibrate(LongArray(0))

        pendingIntent?.let { notificationBuilder.setContentIntent(it) }

        NotificationManagerCompat.from(context)
            .notify(notificationId, notificationBuilder.build())
    }

    fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
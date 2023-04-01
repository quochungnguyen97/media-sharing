package com.rooze.insta_2.presentation.upload

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.use_case.UploadPost
import com.rooze.insta_2.presentation.notification.MediaNotificationManager
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.notification.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val notificationManager: MediaNotificationManager,
    private val uploadPost: UploadPost
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "UploadWorker"
    }

    override suspend fun doWork(): Result {
        val imageUri = inputData.getString(ViewConstants.IMAGE_URI_INPUT_DATA_KEY)
        val postContent = inputData.getString(ViewConstants.POST_CONTENT_INPUT_DATA_KEY)

        Log.i(TAG, "doWork: $imageUri $postContent")

        return if (imageUri != null && postContent != null) {
            Log.i(TAG, "doWork: start uploading")
            notificationManager.makeNotification(
                NotificationType.POSTING_STATUS.getNotificationId(0),
                "Uploading post...",
                android.R.drawable.ic_menu_gallery,
                NotificationCompat.PRIORITY_DEFAULT
            )
            val uploadResult = uploadPost(
                postContent,
                applicationContext.contentResolver.openInputStream(Uri.parse(imageUri))
            )
            Log.i(TAG, "doWork: uploaded $uploadResult")
            notificationManager.cancel(NotificationType.POSTING_STATUS.getNotificationId(0))
            when (uploadResult) {
                is DataResult.Success -> Result.success()
                is DataResult.Fail -> Result.failure(
                    Data.Builder()
                        .putString(ViewConstants.ERROR_MESSAGE_INPUT_DATA_KEY, uploadResult.message)
                        .build()
                )
            }
        } else {
            Result.failure(
                Data.Builder()
                    .putString(ViewConstants.ERROR_MESSAGE_INPUT_DATA_KEY, "Not enough information for upload")
                    .build()
            )
        }
    }
}
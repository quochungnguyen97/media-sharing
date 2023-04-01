package com.rooze.insta_2.presentation.common

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkInfo
import androidx.work.WorkManager

object WorkManagerUtils {
    fun observeUploadPostCompleted(
        applicationContext: Context,
        lifecycleOwner: LifecycleOwner,
        onSuccess: () -> Unit,
        onFailed: (message: String?) -> Unit
    ) {
        WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(ViewConstants.UPLOAD_POST_UNIQUE_WORK_NAME)
            .observe(lifecycleOwner) { workInfos ->
                if (workInfos == null || workInfos.isEmpty()) {
                    return@observe
                }

                val workInfo = workInfos[0]
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> onSuccess()
                    WorkInfo.State.FAILED -> onFailed(
                        workInfo.outputData.getString(ViewConstants.ERROR_MESSAGE_INPUT_DATA_KEY)
                    )
                    else -> Unit
                }
            }
    }
}
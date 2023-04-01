package com.rooze.insta_2.presentation.upload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityUploadBinding
import com.rooze.insta_2.domain.use_case.UploadPost
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {

    private val viewModel: UploadViewModel by viewModels()

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleActivityIntentResult(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUploadBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_upload
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.imageContainer.setOnClickListener {
            activityLauncher.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }
        binding.uploadButton.setOnClickListener {
            postUploadWorker()
        }
    }

    private fun postUploadWorker() {
        val uploadWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setInputData(
                Data.Builder()
                    .putString(ViewConstants.POST_CONTENT_INPUT_DATA_KEY, viewModel.content.get())
                    .putString(ViewConstants.IMAGE_URI_INPUT_DATA_KEY, viewModel.imageUri.value)
                    .build()
            ).setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()

        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                ViewConstants.UPLOAD_POST_UNIQUE_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                uploadWorkRequest
            ).enqueue()

        finish()
    }

    private fun handleActivityIntentResult(intent: Intent) {
        viewModel.setImageUri(intent.data.toString())
    }
}
package com.rooze.insta_2.presentation.edit_account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityEditAccountBinding
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAccountActivity : AppCompatActivity() {
    private val viewModel: EditAccountViewModel by viewModels()

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleActivityIntentResult(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityEditAccountBinding>(
            this,
            R.layout.activity_edit_account
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.avatarContainer.setOnClickListener {
            activityLauncher.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }

        viewModel.message.collectWhenActive(this) { message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.authenticationError.collectWhenActive(this) { isError ->
            if (isError) {
                finish()
            }
        }
        viewModel.edited.collectWhenActive(this) { edited ->
            setResult(
                if (edited) RESULT_OK else RESULT_CANCELED,
                Intent().apply {
                    putExtra(ViewConstants.EXTRA_RELOAD_ACCOUNT, edited)
                    putExtra(ViewConstants.EXTRA_RELOAD_POSTS, edited)
                }
            )
        }
    }

    private fun handleActivityIntentResult(intent: Intent) {
        viewModel.updateImage(intent.data.toString())
    }
}
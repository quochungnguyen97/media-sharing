package com.rooze.insta_2.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityMainBinding
import com.rooze.insta_2.presentation.common.OneTapSignInHelper
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import com.rooze.insta_2.presentation.edit_account.EditAccountActivity
import com.rooze.insta_2.presentation.post_details.PostDetailsActivity
import com.rooze.insta_2.presentation.upload.UploadActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var oneTapSignInHelper: OneTapSignInHelper

    private val navController: NavController get() = findNavController(R.id.nav_host_fragment)

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleActivityIntentResult(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeViewModel()
        initBindingOnClick(binding)
    }

    private fun observeViewModel() {
        viewModel.showLogin.collectWhenActive(this) { showLogin ->
            if (showLogin && navController.currentDestination?.id != R.id.loginFragment) {
                oneTapSignInHelper.signOut()
                navController.navigate(R.id.launch_login)
            }
        }
        viewModel.onOpenPost.collectWhenActive(this) { postId ->
            activityLauncher.launch(
                Intent(
                    applicationContext,
                    PostDetailsActivity::class.java
                ).apply {
                    putExtra(ViewConstants.EXTRA_POST_ID, postId)
                })
        }
        viewModel.onOpenEditAccount.collectWhenActive(this) {
            activityLauncher.launch(Intent(applicationContext, EditAccountActivity::class.java))
        }
    }

    private fun initBindingOnClick(binding: ActivityMainBinding) {
        binding.addNavButton.setOnClickListener {
            activityLauncher.launch(Intent(applicationContext, UploadActivity::class.java))
        }
        binding.postsNavButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.postsListFragment) {
                navController.navigate(R.id.launch_posts_list)
            }
        }
        binding.profileNavButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.userProfileFragment) {
                navController.navigate(
                    R.id.launch_profile,
                    bundleOf("accountId" to viewModel.currentAccount.value?.id)
                )
            }
        }
    }

    private fun handleActivityIntentResult(intent: Intent) {
        if (intent.getBooleanExtra(ViewConstants.EXTRA_RELOAD_POSTS, false)) {
            Log.i(TAG, "handleActivityIntentResult: reload!")
            viewModel.notifyReload("PostsList:posts", "Profile:posts")
        }
        if (intent.getBooleanExtra(ViewConstants.EXTRA_RELOAD_ACCOUNT, false)) {
            viewModel.reloadCurrentAccount()
        }
    }
}
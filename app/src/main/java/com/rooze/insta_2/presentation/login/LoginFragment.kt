package com.rooze.insta_2.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.Identity
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.FragmentLoginBinding
import com.rooze.insta_2.presentation.MainViewModel
import com.rooze.insta_2.presentation.common.OneTapSignInHelper
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import com.rooze.insta_2.presentation.notification.PostNotificationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "LoginFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var oneTapSignInHelper: OneTapSignInHelper

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleSelectImageResult(it) }
        }
    }
    private val oneTapSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
        this::handleOneTapSignInResult
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView: ${findNavController().backQueue.map { it.destination.label }}")

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel

        binding.avatarImage.setOnClickListener {
            selectImageLauncher.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }

        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        binding.oneTapSignInButton.setOnClickListener {
            startOneTapLogin()
        }

        observeViewModel()

        return binding.root
    }

    private fun startOneTapLogin() {
        viewLifecycleOwner.lifecycleScope.launch {
            val requestIntent = oneTapSignInHelper.getSignInRequestIntent()
                ?: oneTapSignInHelper.getSignUpRequestIntent()
            if (requestIntent != null) {
                oneTapSignInLauncher.launch(requestIntent)
            } else {
                Toast.makeText(context, "No account inside device", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun observeViewModel() {
        loginViewModel.message.collectWhenActive(viewLifecycleOwner) { message ->
            context?.applicationContext?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.loggedId.collectWhenActive(viewLifecycleOwner) { loggedIn ->
            if (loggedIn) {
                mainViewModel.reloadCurrentAccount()
                activity?.startService(
                    Intent(
                        activity?.applicationContext,
                        PostNotificationService::class.java
                    ).apply {
                        action = ViewConstants.ACTION_REREGISTER_NOTIFICATION_LISTENER
                    })
                findNavController().navigate(R.id.launch_posts_list_from_login)
            }
        }
    }

    private fun handleSelectImageResult(intent: Intent) {
        Log.i(TAG, "handleActivityIntentResult: ${intent.data}")
        loginViewModel.setImageUri(intent.data.toString())
    }

    private fun handleOneTapSignInResult(result: ActivityResult) {
        try {
            Log.i(TAG, "handleOneTapSignInResult: ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                oneTapSignInHelper.getCredentialTokenId(result.data)?.let { tokenId ->
                    loginViewModel.oneTapLogin(tokenId)
                } ?: Toast.makeText(context, "Credential failed for token id", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "Login dismissed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleOneTapSignInResult: ", e)
            Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
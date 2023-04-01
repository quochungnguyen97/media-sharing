package com.rooze.insta_2.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.FragmentProfileBinding
import com.rooze.insta_2.presentation.MainViewModel
import com.rooze.insta_2.presentation.common.WorkManagerUtils
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class AccountProfileFragment : Fragment(), ProfileImageListener, OnMenuItemClickListener {

    companion object {
        private const val TAG = "AccountProfileFragment"
    }

    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val args: AccountProfileFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadProfileInfo(args.accountId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.menuButton.setOnClickListener {
            val popup = PopupMenu(context, it)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.menu_profile)
            popup.show()
        }

        binding.gridLayoutManager = GridLayoutManager(context, 3)
        binding.adapter = ProfilePostAdapter(this)

        observeViewModel(binding)

        observeWorkManager()

        return binding.root
    }

    private fun observeViewModel(binding: FragmentProfileBinding) {
        mainViewModel.reloadMap.collectWhenActive(viewLifecycleOwner) { map ->
            if (map["Profile:account"] == true) {
                viewModel.loadProfileInfo(args.accountId)
                mainViewModel.markReloaded("Profile:account")
            }
            if (map["Profile:posts"] == true) {
                viewModel.reloadPosts(mainViewModel.currentAccount.value?.id ?: "")
                mainViewModel.markReloaded("Profile:posts")
            }
        }
        combine(viewModel.account, mainViewModel.currentAccount) { account, currentAccount ->
            if (currentAccount == null) {
                false
            } else {
                account.id == currentAccount.id
            }
        }.stateIn(
            viewLifecycleOwner.lifecycleScope,
            SharingStarted.WhileSubscribed(5000),
            false
        ).collectWhenActive(viewLifecycleOwner) { shouldShowMenu ->
            binding.menuButton.visibility = if (shouldShowMenu) View.VISIBLE else View.GONE
        }
    }

    private fun observeWorkManager() {
        context?.applicationContext?.let { applicationContext ->
            WorkManagerUtils.observeUploadPostCompleted(
                applicationContext,
                viewLifecycleOwner,
                { viewModel.reloadPosts(mainViewModel.currentAccount.value?.id ?: "") },
                { message ->
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onClick(postId: String) {
        mainViewModel.openPost(postId)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_account -> {
                Log.i(TAG, "onMenuItemClick: edit")
                mainViewModel.openEditAccount()
            }
            R.id.logout -> {
                mainViewModel.logout()
            }
            else -> return false
        }
        return true
    }
}
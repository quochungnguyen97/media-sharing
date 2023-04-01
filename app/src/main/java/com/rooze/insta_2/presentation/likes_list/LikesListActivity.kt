package com.rooze.insta_2.presentation.likes_list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityLikesListBinding
import com.rooze.insta_2.dependency_injection.viewmodel.LikesListViewModelAssistedFactory
import com.rooze.insta_2.presentation.common.ViewConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LikesListActivity : AppCompatActivity() {

    @Inject
    lateinit var assistedFactory: LikesListViewModelAssistedFactory

    private val viewModel: LikesListViewModel by viewModels {
        LikesListViewModelFactory(
            assistedFactory,
            intent?.getStringExtra(ViewConstants.EXTRA_POST_ID) ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityLikesListBinding>(
            this,
            R.layout.activity_likes_list
        )

        binding.lifecycleOwner = this
        binding.likesViewModel = viewModel
        binding.adapter = LikesAccountAdapter()

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
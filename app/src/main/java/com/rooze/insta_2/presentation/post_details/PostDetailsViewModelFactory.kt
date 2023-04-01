package com.rooze.insta_2.presentation.post_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rooze.insta_2.dependency_injection.viewmodel.PostDetailsViewModelAssistedFactory

class PostDetailsViewModelFactory(
    private val assistedFactory: PostDetailsViewModelAssistedFactory,
    private val postId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.createPostDetailsViewModel(postId) as T
    }
}
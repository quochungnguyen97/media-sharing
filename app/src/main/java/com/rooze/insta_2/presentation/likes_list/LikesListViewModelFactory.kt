package com.rooze.insta_2.presentation.likes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rooze.insta_2.dependency_injection.viewmodel.LikesListViewModelAssistedFactory

class LikesListViewModelFactory(
    private val assistedFactory: LikesListViewModelAssistedFactory,
    private val postId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.createLikesListViewModel(postId) as T
    }
}
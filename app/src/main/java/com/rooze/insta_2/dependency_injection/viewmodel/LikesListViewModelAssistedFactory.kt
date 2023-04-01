package com.rooze.insta_2.dependency_injection.viewmodel

import com.rooze.insta_2.presentation.likes_list.LikesListViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LikesListViewModelAssistedFactory {
    fun createLikesListViewModel(postId: String): LikesListViewModel
}
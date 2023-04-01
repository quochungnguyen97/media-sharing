package com.rooze.insta_2.dependency_injection.viewmodel

import com.rooze.insta_2.presentation.post_details.PostDetailsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface PostDetailsViewModelAssistedFactory {
    fun createPostDetailsViewModel(postId: String): PostDetailsViewModel
}
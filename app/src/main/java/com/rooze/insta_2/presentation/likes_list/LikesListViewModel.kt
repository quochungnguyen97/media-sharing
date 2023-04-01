package com.rooze.insta_2.presentation.likes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.use_case.LikesList
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikesListViewModel @AssistedInject constructor(
    private val likesList: LikesList,
    @Assisted
    private val postId: String
) : ViewModel() {
    private val _likeAccounts = MutableStateFlow(emptyList<Account>())
    val likeAccounts get(): StateFlow<List<Account>> = _likeAccounts

    init {
        viewModelScope.launch {
            _likeAccounts.value = likesList.getLikesList(postId).successDataOrNull() ?: emptyList()
        }
    }
}
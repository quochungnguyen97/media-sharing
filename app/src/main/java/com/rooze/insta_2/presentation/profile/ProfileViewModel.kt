package com.rooze.insta_2.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.domain.use_case.ProfileInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileInfo: ProfileInfo
) : ViewModel() {

    private val _account = MutableStateFlow(Account())
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _loading = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()

    val account get(): StateFlow<Account> = _account
    val posts get(): StateFlow<List<Post>> = _posts
    val loading get(): StateFlow<Boolean> = _loading
    val message get(): SharedFlow<String> = _message

    fun loadProfileInfo(accountId: String) {
        if (loading.value) {
            return
        }
        _loading.value = true
        viewModelScope.launch {
            when (val accountResult = profileInfo.getAccountInfo(accountId)) {
                is DataResult.Fail -> _message.emit(accountResult.message)
                is DataResult.Success -> {
                    _account.value = accountResult.data
                    loadPosts(accountId)
                }
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun reloadPosts(accountId: String) {
        if (loading.value) {
            return
        }

        _loading.value = true
        viewModelScope.launch {
            loadPosts(accountId)
        }.invokeOnCompletion { _loading.value = false }
    }

    private suspend fun loadPosts(accountId: String) {
        when (val postResult = profileInfo.getProfilePosts(accountId)) {
            is DataResult.Fail -> _message.emit(postResult.message)
            is DataResult.Success -> _posts.value = postResult.data
        }
    }
}
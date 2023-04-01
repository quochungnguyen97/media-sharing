package com.rooze.insta_2.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.use_case.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authentication: Authentication) : ViewModel(){

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _showLogin = MutableStateFlow(false)
    private val _currentAccount = MutableStateFlow<Account?>(null)
    private val _reloadMap = MutableStateFlow<MutableMap<String, Boolean>>(hashMapOf())

    private val _onOpenPost = MutableSharedFlow<String>()
    private val _onOpenEditAccount = MutableSharedFlow<Boolean>()

    val showLogin get(): StateFlow<Boolean> = _showLogin
    val currentAccount get(): StateFlow<Account?> = _currentAccount
    val reloadMap get(): StateFlow<Map<String, Boolean>> = _reloadMap

    val onOpenPost get(): SharedFlow<String> = _onOpenPost
    val onOpenEditAccount get(): SharedFlow<Boolean> = _onOpenEditAccount

    init {
        reloadCurrentAccount()
    }

    fun reloadCurrentAccount() {
        viewModelScope.launch {
            val accountResult = authentication.getCurrentAccount()
            if (accountResult is DataResult.Success) {
                _currentAccount.value = accountResult.data
                notifyReload("Profile:account")
                _showLogin.value = false
            } else {
                _currentAccount.value = null
                _showLogin.value = true
            }
        }
    }

    fun notifyReload(vararg keys: String) {
        Log.i(TAG, "notifyReload: ${reloadMap.value.hashCode()}")
        _reloadMap.value = HashMap(reloadMap.value).apply {
            for (key in keys) {
                put(key, true)
            }
        }
        Log.i(TAG, "notifyReload: ${reloadMap.value.hashCode()}")
    }

    fun markReloaded(key: String) {
        _reloadMap.value = HashMap(reloadMap.value).apply { put(key, false) }
    }

    fun openPost(postId: String) {
        viewModelScope.launch {
            _onOpenPost.emit(postId)
        }
    }

    fun openEditAccount() {
        viewModelScope.launch {
            _onOpenEditAccount.emit(true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authentication.logout()
            reloadCurrentAccount()
        }
    }
}
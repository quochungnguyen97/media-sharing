package com.rooze.insta_2.presentation.edit_account

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.use_case.Authentication
import com.rooze.insta_2.domain.use_case.EditAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val editAccount: EditAccount,
    private val authentication: Authentication
) : ViewModel() {
    private val _currentAccount = MutableStateFlow(Account())
    private val _authenticationError = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()
    private val _updating = MutableStateFlow(false)
    private var _edited = MutableStateFlow(false)
    private val _editedAvatarUri = MutableStateFlow("")

    private val currentAccount get(): StateFlow<Account> = _currentAccount
    val authenticationError get(): StateFlow<Boolean> = _authenticationError
    val message get(): SharedFlow<String> = _message
    val updating get(): StateFlow<Boolean> = _updating
    val edited get(): StateFlow<Boolean> = _edited
    val editedAccountName = ObservableField("")
    val editedAvatarUri get(): StateFlow<String> = _editedAvatarUri

    init {
        _updating.value = true
        viewModelScope.launch {
            reloadCurrentAccount()
        }.invokeOnCompletion {
            _updating.value = false
        }
    }

    fun updateInfo(context: Context) {
        if (updating.value || authenticationError.value) {
            return
        }

        _updating.value = true
        viewModelScope.launch {
            val name = if (currentAccount.value.name == editedAccountName.get()) {
                null
            } else {
                editedAccountName.get()
            }
            val avatar = if (currentAccount.value.avatarUrl == editedAvatarUri.value) {
                null
            } else {
                context.contentResolver.openInputStream(Uri.parse(editedAvatarUri.value))
            }

            when (val result = editAccount.editAccountInfo(name, avatar)) {
                is DataResult.Fail -> _message.emit(result.message)
                is DataResult.Success -> {
                    _edited.value = true
                    reloadCurrentAccount()
                }
            }
        }.invokeOnCompletion { _updating.value = false }
    }

    fun updateImage(uri: String) {
        _editedAvatarUri.value = uri
    }

    private suspend fun reloadCurrentAccount() {
        _currentAccount.value = authentication.getCurrentAccount().let {
            when (it) {
                is DataResult.Fail -> {
                    _authenticationError.value = true
                    Account()
                }
                is DataResult.Success -> {
                    editedAccountName.set(it.data.name)
                    _editedAvatarUri.value = it.data.avatarUrl
                    it.data
                }
            }
        }
    }
}
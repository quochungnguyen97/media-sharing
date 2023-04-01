package com.rooze.insta_2.presentation.login

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.use_case.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authentication: Authentication
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _imageUri = MutableStateFlow("")
    private val _viewState = MutableStateFlow(LoginViewState.LOGIN)
    private val _loggedIn = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()
    private val _loading = MutableStateFlow(false)

    val email = ObservableField("")
    val password = ObservableField("")
    val password2 = ObservableField("")
    val displayName = ObservableField("")
    val imageUri get(): StateFlow<String> = _imageUri
    val viewState get(): StateFlow<LoginViewState> = _viewState
    val loggedId get(): StateFlow<Boolean> = _loggedIn
    val message get(): SharedFlow<String> = _message
    val loading get(): StateFlow<Boolean> = _loading

    fun submit(context: Context) {
        when (viewState.value) {
            LoginViewState.LOGIN -> login()
            LoginViewState.REGISTER -> register(context)
        }
    }

    private fun login() {
        _loading.value = true
        viewModelScope.launch {
            when (val result = authentication.login(email.get() ?: "", password.get() ?: "")) {
                is DataResult.Success -> {
                    _message.emit("Logged in")
                    _loggedIn.value = true
                }
                is DataResult.Fail -> _message.emit(result.message)
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun oneTapLogin(tokenId: String) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = authentication.oneTapLogin(tokenId)) {
                is DataResult.Success -> {
                    _message.emit("Logged in")
                    _loggedIn.value = true
                }
                is DataResult.Fail -> _message.emit(result.message)
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    private fun register(context: Context) {
        _loading.value = true
        viewModelScope.launch {
            val result = authentication.register(
                name = displayName.get() ?: "",
                email = email.get() ?: "",
                password = password.get() ?: "",
                password2 = password2.get() ?: "",
                avatarInputStream = if (imageUri.value.isEmpty()) {
                    null
                } else {
                    context.contentResolver.openInputStream(Uri.parse(imageUri.value))
                }
            )
            when (result) {
                is DataResult.Success -> {
                    if (result.data.avatarUrl.isEmpty()) {
                        _message.emit("Register success without avatar")
                    } else {
                        _message.emit("Register success")
                    }
                    changeState()
                }
                is DataResult.Fail -> {
                    _message.emit(result.message)
                }
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun changeState() {
        _viewState.value = if (viewState.value == LoginViewState.LOGIN) {
            LoginViewState.REGISTER
        } else {
            LoginViewState.LOGIN
        }
    }

    fun setImageUri(uri: String) {
        _imageUri.value = uri
    }
}
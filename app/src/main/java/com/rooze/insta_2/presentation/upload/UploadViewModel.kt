package com.rooze.insta_2.presentation.upload

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UploadViewModel : ViewModel() {

    companion object {
        private const val TAG = "UploadViewModel"
    }

    private val _imageUri = MutableStateFlow("")

    val content = ObservableField("")
    val imageUri get(): StateFlow<String> = _imageUri

    fun setImageUri(uri: String) {
        _imageUri.value = uri
    }
}
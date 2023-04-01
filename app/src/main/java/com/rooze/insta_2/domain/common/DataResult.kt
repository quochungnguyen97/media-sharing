package com.rooze.insta_2.domain.common

sealed class DataResult<T> {
    class Fail<T>(val message: String, val exception: Exception? = null) : DataResult<T>()
    class Success<T>(val data: T) : DataResult<T>()

    fun successDataOrNull(): T? {
        return when (this) {
            is Success -> data
            is Fail -> null
        }
    }
}
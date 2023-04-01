package com.rooze.insta_2.presentation.posts_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.use_case.AddNotification
import com.rooze.insta_2.domain.use_case.PostsList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postsList: PostsList,
    private val addNotification: AddNotification
) : ViewModel() {
    companion object {
        private const val TAG = "PostsViewModel"
    }

    private val _posts = MutableStateFlow<List<PostComments>>(emptyList())
    private val _loading = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()
    private val isLiking = AtomicBoolean()

    val posts get(): StateFlow<List<PostComments>> = _posts
    val loading get(): StateFlow<Boolean> = _loading
    val message get(): SharedFlow<String> = _message

    init {
        reloadPosts()
    }

    fun loadMore() {
        if (_loading.value) {
            return
        }
        _loading.value = true
        viewModelScope.launch {
            when (val postsResult = postsList.loadMorePosts()) {
                is DataResult.Success -> _posts.value = postsResult.data
                is DataResult.Fail -> _message.emit(postsResult.message)
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun reloadPosts() {
        if (_loading.value) {
            return
        }
        Log.i(TAG, "reloadPosts: ")
        _loading.value = true
        viewModelScope.launch {
            when (val postsResult = postsList.reloadPostsList()) {
                is DataResult.Success -> _posts.value = postsResult.data
                is DataResult.Fail -> _message.emit(postsResult.message)
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun like(postId: String) {
        Log.i(TAG, "like: $postId ${isLiking.get()}")
        if (isLiking.get()) {
            return
        }
        isLiking.set(true)
        viewModelScope.launch {
            when (val likeResult = postsList.like(postId)) {
                is DataResult.Success -> {
                    Log.i(TAG, "like: ${likeResult.data.map { it.post.id }} $postId")
                    _posts.value = likeResult.data
                    likeResult.data.find { postId == it.post.id }?.post?.let {
                        addNotification.postLikeNotification(it)
                    }
                }
                is DataResult.Fail -> _message.emit(likeResult.message)
            }
        }.invokeOnCompletion { isLiking.set(false) }
    }
}
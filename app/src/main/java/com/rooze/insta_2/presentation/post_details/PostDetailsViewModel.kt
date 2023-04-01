package com.rooze.insta_2.presentation.post_details

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.domain.use_case.AddNotification
import com.rooze.insta_2.domain.use_case.Authentication
import com.rooze.insta_2.domain.use_case.PostDetails
import com.rooze.insta_2.presentation.post_details.view_holder.DetailItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PostDetailsViewModel @AssistedInject constructor(
    private val postDetails: PostDetails,
    private val authentication: Authentication,
    private val addNotification: AddNotification,
    @Assisted
    private val postId: String
) : ViewModel() {
    private val _post = MutableStateFlow(PostComments())
    private val _loading = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()
    private var _shouldReload = MutableStateFlow(false)
    private val _deleted = MutableStateFlow(false)
    private val _currentAccount = MutableStateFlow(Account())

    val post get(): StateFlow<PostComments> = _post
    val loading get(): StateFlow<Boolean> = _loading
    val message get(): SharedFlow<String> = _message
    val shouldReload get(): StateFlow<Boolean> = _shouldReload
    val deleted get(): StateFlow<Boolean> = _deleted
    val currentAccount get(): StateFlow<Account> = _currentAccount
    val isEditable
        get(): StateFlow<Boolean> = post.map {
            it.post.owner.id == currentAccount.value.id
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    val items
        get(): StateFlow<List<DetailItem>> = post.map {
            ArrayList<DetailItem>().apply {
                add(DetailItem.DetailItemAccount(it.post.owner))
                add(DetailItem.Content(it.post.content))
                add(DetailItem.Image(it.post.imageUrl))
                add(
                    DetailItem.CommentsLikes(
                        liked = it.liked,
                        commentsCount = it.comments.size,
                        likesCount = it.post.likes.size
                    )
                )
                for (comment in it.comments) {
                    add(DetailItem.DetailItemComment(comment))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val commentContent = ObservableField("")

    private val _isLiking = AtomicBoolean(false)
    private val isLiking get(): Boolean = _isLiking.get()

    private val _isCommenting = AtomicBoolean(false)
    private val isCommenting get(): Boolean = _isCommenting.get()

    private val _isDeleting = AtomicBoolean(false)
    private val isDeleting get(): Boolean = _isDeleting.get()

    init {
        viewModelScope.launch {
            authentication.getCurrentAccount().let {
                when (it) {
                    is DataResult.Fail -> _message.emit("Not logged in")
                    is DataResult.Success -> _currentAccount.value = it.data
                }
            }
        }
        reloadPost()
    }

    fun reloadPost() {
        if (_loading.value) {
            return
        }

        _loading.value = true
        viewModelScope.launch {
            when (val postResult = postDetails.getPostDetails(postId)) {
                is DataResult.Fail -> _message.emit(postResult.message)
                is DataResult.Success -> _post.value = postResult.data
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun like() {
        if (isLiking) {
            return
        }
        _isLiking.set(true)
        viewModelScope.launch {
            when (val likeResult = postDetails.like()) {
                is DataResult.Fail -> _message.emit(likeResult.message)
                is DataResult.Success -> {
                    _post.value = likeResult.data
                    _shouldReload.value = true
                    addNotification.postLikeNotification(likeResult.data.post)
                }
            }
        }.invokeOnCompletion { _isLiking.set(false) }
    }

    fun comment() {
        if (isCommenting) {
            return
        }
        viewModelScope.launch {
            when (val commentResult = postDetails.comment(commentContent.get() ?: "")) {
                is DataResult.Fail -> _message.emit(commentResult.message)
                is DataResult.Success -> {
                    commentContent.set("")
                    _post.value = commentResult.data
                    _shouldReload.value = true
                    addNotification.postCommentNotification(
                        commentResult.data.comments.last(),
                        commentResult.data.post.owner.id
                    )
                }
            }
        }.invokeOnCompletion { _isCommenting.set(false) }
    }

    fun delete() {
        if (isDeleting) {
            return
        }

        viewModelScope.launch {
            when (val deleteResult = postDetails.delete()) {
                is DataResult.Fail -> _message.emit(deleteResult.message)
                is DataResult.Success -> {
                    _message.emit("Deleted")
                    _shouldReload.value = true
                    _deleted.value = true
                }
            }
        }.invokeOnCompletion { _isDeleting.set(false) }
    }
}
package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.Comment

const val ACCOUNT = 1
const val IMAGE = 2
const val COMMENTS_LIKES = 3
const val COMMENT = 4
const val ADD_COMMENT = 5
const val CONTENT = 6

sealed class DetailItem(val type: Int) {
    class DetailItemAccount(val account: Account = Account()) : DetailItem(ACCOUNT)
    class Image(val imageUrl: String = "") : DetailItem(IMAGE)
    class CommentsLikes(
        val liked: Boolean = false,
        val likesCount: Int = 0,
        val commentsCount: Int = 0
    ) : DetailItem(COMMENTS_LIKES)
    class DetailItemComment(val comment: Comment = Comment()) : DetailItem(COMMENT)
    object AddComment : DetailItem(ADD_COMMENT)
    class Content(val content: String) : DetailItem(CONTENT)
}

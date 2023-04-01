package com.rooze.insta_2.domain.entity

sealed class Notification(val receivingAccountId: String) {
    class Comment(
        receivingAccountId: String,
        val commentId: String,
        var comment: com.rooze.insta_2.domain.entity.Comment? = null,
        var post: Post? = null
    ) : Notification(receivingAccountId)

    class Like(
        receivingAccountId: String,
        val postId: String,
        val likerId: String,
        var post: Post? = null,
        var liker: Account? = null
    ) : Notification(receivingAccountId)
}

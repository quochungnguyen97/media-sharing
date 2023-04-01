package com.rooze.insta_2.domain.entity

data class PostComments(
    val post: Post = Post(),
    val comments: List<Comment> = emptyList(),
    val liked: Boolean = false
)
package com.rooze.insta_2.domain.entity

data class Comment(
    val id: String = "",
    val postId: String = "",
    val owner: Account = Account(),
    val content: String = "",
    val time: Long = System.currentTimeMillis()
)
package com.rooze.insta_2.domain.entity

data class Post(
    val id: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val owner: Account = Account(),
    val likes: List<Account> = emptyList(),
    val time: Long = System.currentTimeMillis(),
)

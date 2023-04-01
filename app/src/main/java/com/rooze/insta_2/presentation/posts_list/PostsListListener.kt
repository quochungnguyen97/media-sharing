package com.rooze.insta_2.presentation.posts_list

interface PostsListListener {
    fun like(postId: String)
    fun openComment(postId: String)
    fun openPost(postId: String)
    fun openAccount(accountId: String)
    fun openLikesList(postId: String)
}
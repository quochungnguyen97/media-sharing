package com.rooze.insta_2.presentation.posts_list

import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rooze.insta_2.databinding.ItemPostBinding
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.PostComments

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val postsListListener: PostsListListener,
) : ViewHolder(binding.root) {
    companion object {
        private const val TAG = "PostViewHolder"
    }
    fun bind(post: PostComments) {
        Log.i(TAG, "bind: $post")
        binding.post = post
        binding.postsListListener = postsListListener
    }
}
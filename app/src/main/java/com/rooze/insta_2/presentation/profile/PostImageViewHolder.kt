package com.rooze.insta_2.presentation.profile

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rooze.insta_2.databinding.ItemPostImageBinding
import com.rooze.insta_2.domain.entity.Post

class PostImageViewHolder(
    private val postImageListener: ProfileImageListener,
    private val binding: ItemPostImageBinding
) : ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.post = post
        itemView.setOnClickListener {
            postImageListener.onClick(post.id)
        }
    }
}
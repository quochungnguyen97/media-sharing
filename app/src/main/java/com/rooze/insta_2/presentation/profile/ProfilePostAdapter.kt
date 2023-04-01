package com.rooze.insta_2.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rooze.insta_2.databinding.ItemPostImageBinding
import com.rooze.insta_2.domain.entity.Post
import com.rooze.insta_2.presentation.common.BaseAdapter

class ProfilePostAdapter(
    private val postImageListener: ProfileImageListener
) : BaseAdapter<Post, PostImageViewHolder>() {

    private val posts: MutableList<Post> = ArrayList()

    override fun setItems(items: List<Post>) {
        posts.clear()
        posts.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        return PostImageViewHolder(
            postImageListener,
            ItemPostImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
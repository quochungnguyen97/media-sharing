package com.rooze.insta_2.presentation.posts_list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rooze.insta_2.databinding.ItemPostBinding
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.entity.PostComments
import com.rooze.insta_2.presentation.common.BaseAdapter

class PostsAdapter(
    private val postsListListener: PostsListListener
) : BaseAdapter<PostComments, PostViewHolder>() {
    private val posts: MutableList<PostComments> = arrayListOf()

    override fun setItems(items: List<PostComments>) {
        posts.clear()
        posts.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            postsListListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
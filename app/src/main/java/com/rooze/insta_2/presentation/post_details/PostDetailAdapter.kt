package com.rooze.insta_2.presentation.post_details

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rooze.insta_2.databinding.*
import com.rooze.insta_2.presentation.common.BaseAdapter
import com.rooze.insta_2.presentation.post_details.view_holder.*

class PostDetailAdapter(private val detailsViewListener: DetailsViewListener) : BaseAdapter<DetailItem, BaseDetailViewHolder<in DetailItem>>() {
    private val items: MutableList<DetailItem> = arrayListOf()

    override fun setItems(items: List<DetailItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDetailViewHolder<in DetailItem> {
        return when (viewType) {
            ACCOUNT -> AccountViewHolder(
                ItemDetailAccountBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) as BaseDetailViewHolder<in DetailItem>
            CONTENT -> ContentViewHolder(
                ItemDetailContentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) as BaseDetailViewHolder<in DetailItem>
            IMAGE -> ImageViewHolder(
                ItemDetailImageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) as BaseDetailViewHolder<in DetailItem>
            COMMENTS_LIKES -> CommentsLikesViewHolder(
                detailsViewListener,
                ItemDetailCommentsLikesBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) as BaseDetailViewHolder<in DetailItem>
            COMMENT -> CommentViewHolder(
                ItemDetailCommentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) as BaseDetailViewHolder<in DetailItem>
            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseDetailViewHolder<in DetailItem>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

}
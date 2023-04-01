package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.databinding.ItemDetailCommentBinding

class CommentViewHolder(
    private val binding: ItemDetailCommentBinding
) : BaseDetailViewHolder<DetailItem.DetailItemComment>(binding.root) {
    override fun bind(item: DetailItem.DetailItemComment) {
        binding.comment = item.comment
    }
}
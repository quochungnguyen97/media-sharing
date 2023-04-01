package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.databinding.ItemDetailCommentsLikesBinding

class CommentsLikesViewHolder(
    private val detailsViewListener: DetailsViewListener,
    private val binding: ItemDetailCommentsLikesBinding
) : BaseDetailViewHolder<DetailItem.CommentsLikes>(binding.root) {
    init {
        binding.likeButton.setOnClickListener {
            detailsViewListener.like()
        }
        binding.likesCommentsText.setOnClickListener {
            detailsViewListener.openLikesList()
        }
    }

    override fun bind(item: DetailItem.CommentsLikes) {
        binding.liked = item.liked
        binding.commentsCount = item.commentsCount
        binding.likesCount = item.likesCount
    }
}
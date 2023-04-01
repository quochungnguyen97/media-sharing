package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.databinding.ItemDetailImageBinding

class ImageViewHolder(
    private val binding: ItemDetailImageBinding
) : BaseDetailViewHolder<DetailItem.Image>(binding.root) {
    override fun bind(item: DetailItem.Image) {
        binding.imageUrl = item.imageUrl
    }
}
package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.databinding.ItemDetailContentBinding

class ContentViewHolder(
    private val binding: ItemDetailContentBinding
) : BaseDetailViewHolder<DetailItem.Content>(binding.root) {
    override fun bind(item: DetailItem.Content) {
        binding.content = item.content
    }
}
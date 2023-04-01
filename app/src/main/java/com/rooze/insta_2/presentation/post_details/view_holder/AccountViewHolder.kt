package com.rooze.insta_2.presentation.post_details.view_holder

import com.rooze.insta_2.databinding.ItemDetailAccountBinding

class AccountViewHolder(
    private val binding: ItemDetailAccountBinding
) : BaseDetailViewHolder<DetailItem.DetailItemAccount>(binding.root) {
    override fun bind(item: DetailItem.DetailItemAccount) {
        binding.account = item.account
    }
}
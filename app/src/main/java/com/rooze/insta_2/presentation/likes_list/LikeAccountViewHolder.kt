package com.rooze.insta_2.presentation.likes_list

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rooze.insta_2.databinding.ItemLikeAccountBinding
import com.rooze.insta_2.domain.entity.Account

class LikeAccountViewHolder(
    private val binding: ItemLikeAccountBinding
) : ViewHolder(binding.root) {
    fun bind(account: Account) {
        binding.account = account
    }
}
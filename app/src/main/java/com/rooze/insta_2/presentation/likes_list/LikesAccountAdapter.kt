package com.rooze.insta_2.presentation.likes_list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rooze.insta_2.databinding.ItemLikeAccountBinding
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.presentation.common.BaseAdapter

class LikesAccountAdapter : BaseAdapter<Account, LikeAccountViewHolder>() {
    private val accounts: MutableList<Account> = ArrayList()

    override fun setItems(items: List<Account>) {
        accounts.clear()
        accounts.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeAccountViewHolder {
        return LikeAccountViewHolder(ItemLikeAccountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: LikeAccountViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount(): Int {
        return accounts.size
    }
}
package com.rooze.insta_2.presentation.post_details.view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class BaseDetailViewHolder<DI : DetailItem>(view: View) : ViewHolder(view) {
    abstract fun bind(item: DI)
}
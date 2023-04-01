package com.rooze.insta_2.presentation.common

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

private const val TAG = "BaseAdapter"

abstract class BaseAdapter<Item, VH : ViewHolder> : Adapter<VH>() {
    abstract fun setItems(items: List<Item>)
}

@BindingAdapter("items")
fun <Item> updateItems(view: RecyclerView, items: List<Item>?) {
    Log.i(TAG, "updateItems: $items")
    if (items != null) {
        (view.adapter as? BaseAdapter<Item, in ViewHolder>)?.setItems(items)
    }
}
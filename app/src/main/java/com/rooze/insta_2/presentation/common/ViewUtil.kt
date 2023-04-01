package com.rooze.insta_2.presentation.common

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl", "error")
fun loadImage(view: ImageView, url: String, error: Drawable) {
    if (url.isNotEmpty()) {
        Picasso.get().load(url).error(error).into(view)
    } else {
        view.setImageDrawable(error)
    }
}

@BindingAdapter("isRefreshing")
fun setIsRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean) {
    view.isRefreshing = isRefreshing
}

@BindingAdapter("onRefresh")
fun setOnRefresh(view: SwipeRefreshLayout, onRefresh: () -> Unit) {
    view.setOnRefreshListener(onRefresh)
}

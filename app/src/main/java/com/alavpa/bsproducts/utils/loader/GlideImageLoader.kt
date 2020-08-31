package com.alavpa.bsproducts.utils.loader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

class GlideImageLoader : ImageLoader {

    override fun load(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }
}

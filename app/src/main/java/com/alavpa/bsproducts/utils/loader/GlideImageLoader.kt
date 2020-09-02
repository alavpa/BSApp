package com.alavpa.bsproducts.utils.loader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL

class GlideImageLoader : ImageLoader {

    override fun load(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).override(SIZE_ORIGINAL).into(imageView)
    }

    override fun load(id: Int, imageView: ImageView) {
        imageView.setImageResource(id)
    }
}

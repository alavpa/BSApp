package com.alavpa.bsproducts.utils.loader

import android.content.Context
import android.widget.ImageView

interface ImageLoader {
    fun load(context: Context, url: String, imageView: ImageView)
    fun load(id: Int, imageView: ImageView)
}

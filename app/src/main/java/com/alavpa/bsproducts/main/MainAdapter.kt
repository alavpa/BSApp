package com.alavpa.bsproducts.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.alavpa.bsproducts.R
import com.alavpa.bsproducts.presentation.model.ProductItem
import com.alavpa.bsproducts.utils.loader.ImageLoader

class MainAdapter(
    private val context: Context,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<MainAdapter.Item>() {

    class Item(
        private val context: Context,
        private val imageLoader: ImageLoader,
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val imageView by lazy { itemView.findViewById<ImageView>(R.id.image) }
        fun bind(item: ProductItem) {
            imageLoader.load(
                context,
                item.image,
                imageView
            )
        }
    }

    private val items = mutableListOf<ProductItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(
            context,
            imageLoader,
            LayoutInflater.from(context).inflate(R.layout.view_product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun load(items: List<ProductItem>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
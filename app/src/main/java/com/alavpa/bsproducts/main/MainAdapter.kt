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
    private val imageLoader: ImageLoader,
    private val clickOn: (ProductItem) -> Unit
) : RecyclerView.Adapter<MainAdapter.Item>() {

    class Item(
        private val context: Context,
        private val imageLoader: ImageLoader,
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val imageView by lazy { itemView.findViewById<ImageView>(R.id.image) }
        private val likeView by lazy { itemView.findViewById<ImageView>(R.id.like) }
        fun bind(item: ProductItem) {
            imageLoader.load(
                context,
                item.image,
                imageView
            )

            if (item.liked) imageLoader.load(context, R.drawable.ic_baseline_favorite_24, likeView)
            else imageLoader.load(context, R.drawable.ic_baseline_favorite_border_24, likeView)
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
        holder.itemView.setOnClickListener {
            clickOn(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun load(items: List<ProductItem>) {
        val range = items.size - this.items.size
        when {
            range > 0 -> {
                this.items.clear()
                this.items.addAll(items)
                this.notifyItemRangeInserted(this.items.size, range)
            }

            range < 0 -> {
                this.items.clear()
                this.items.addAll(items)
                this.notifyDataSetChanged()
            }
        }
    }
}

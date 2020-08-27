package com.alavpa.bsproducts.presentation.model

import com.alavpa.bsproducts.domain.model.Product

data class ProductItem(
    val id: Long,
    val name: String,
    val brand: String,
    val price: Int,
    val currency: String,
    val image: String
)

fun Product.toItem(): ProductItem {
    return ProductItem(
        this.id,
        this.name,
        this.brand,
        this.price,
        this.currency,
        this.image
    )
}

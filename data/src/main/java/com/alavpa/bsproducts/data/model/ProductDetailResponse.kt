package com.alavpa.bsproducts.data.model

import com.alavpa.bsproducts.domain.model.Product

data class ProductDetailResponse(
    val id: Long,
    val name: String,
    val description: String,
    val brand: String,
    val price: Int,
    val currency: String,
    val discountPercentage: Int,
    val image: String,
    val stock: Int
)

fun ProductDetailResponse.toProduct(): Product = Product(
    this.id,
    this.name,
    this.description,
    this.brand,
    this.price,
    this.currency,
    this.discountPercentage,
    this.image,
    this.stock
)

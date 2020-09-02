package com.alavpa.bsproducts.data.model

import com.alavpa.bsproducts.domain.model.Product

data class ProductItemResponse(
    val id: Long,
    val name: String,
    val brand: String,
    val price: Int,
    val currency: String,
    val image: String
)

fun ProductItemResponse.toProduct(): Product = Product(
    this.id,
    this.name,
    "",
    this.brand,
    this.price,
    this.currency,
    0,
    this.image,
    0
)

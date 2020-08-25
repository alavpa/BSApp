package com.alavpa.bsproducts.domain.model

data class Product(
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

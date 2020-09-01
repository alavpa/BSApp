package com.alavpa.bsproducts.data.model

data class PageResponse(
    val list: List<ProductItemResponse>,
    val size: Int
)

package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.ProductItemResponse
import io.reactivex.Single

interface DataSource {
    fun getItems(page: Int, size: Int): Single<List<ProductItemResponse>>
}

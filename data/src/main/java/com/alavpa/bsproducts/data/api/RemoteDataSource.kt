package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.ProductDetailsResponse
import com.alavpa.bsproducts.data.model.ProductItemResponse
import io.reactivex.Single

interface RemoteDataSource {
    fun getItems(page: Int, size: Int): Single<List<ProductItemResponse>>
    fun getProductDetails(productId: Long): Single<ProductDetailsResponse>
}

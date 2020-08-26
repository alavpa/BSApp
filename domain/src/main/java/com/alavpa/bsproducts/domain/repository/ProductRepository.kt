package com.alavpa.bsproducts.domain.repository

import com.alavpa.bsproducts.domain.model.Product
import io.reactivex.Completable
import io.reactivex.Single

interface ProductRepository {
    fun getProducts(page: Int, size: Int): Single<List<Product>>
    fun getProductById(id: Long): Single<Product>
    fun addToCart(productId: Long): Completable
}

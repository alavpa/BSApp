package com.alavpa.bsproducts.data

import com.alavpa.bsproducts.data.api.DataSource
import com.alavpa.bsproducts.data.model.toProduct
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Completable
import io.reactivex.Single

data class ProductDataRepository(private val apiDataSource: DataSource) : ProductRepository {
    override fun getProducts(page: Int, size: Int): Single<List<Product>> {
        return apiDataSource.getItems(page, size).map { list -> list.map { it.toProduct() } }
    }

    override fun getProductById(id: Long): Single<Product> {
        TODO("Not yet implemented")
    }

    override fun addToCart(productId: Long): Completable {
        TODO("Not yet implemented")
    }
}

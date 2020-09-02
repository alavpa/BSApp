package com.alavpa.bsproducts.data

import com.alavpa.bsproducts.data.api.RemoteDataSource
import com.alavpa.bsproducts.data.local.LocalDataSource
import com.alavpa.bsproducts.data.model.toProduct
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Completable
import io.reactivex.Single

data class ProductDataRepository(
    private val apiDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ProductRepository {
    override fun getProducts(page: Int, size: Int): Single<List<Product>> {
        return apiDataSource.getItems(page, size).map { list -> list.map { it.toProduct() } }
    }

    override fun getProductById(id: Long): Single<Product> {
        return apiDataSource.getProductDetails(id).map { it.toProduct() }
    }

    override fun addToCart(productId: Long): Completable {
        return Completable.complete()
    }

    override fun like(id: Long): Completable {
        return Completable.fromCallable { localDataSource.like(id) }
    }

    override fun dislike(id: Long): Completable {
        return Completable.fromCallable { localDataSource.dislike(id) }
    }

    override fun likes(): Single<List<Long>> {
        return Single.fromCallable { localDataSource.likes() }
    }
}

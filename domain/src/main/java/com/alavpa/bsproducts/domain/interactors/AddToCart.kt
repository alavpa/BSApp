package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.error.NoStockException
import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Completable

class AddToCart(private val repository: ProductRepository) : Interactor<Completable> {
    var productId: Long = 0
    override fun build(): Completable {
        return repository.getProductById(productId).flatMapCompletable { product ->
            if (product.stock > 0) repository.addToCart(productId)
            else Completable.error(NoStockException())
        }
    }
}

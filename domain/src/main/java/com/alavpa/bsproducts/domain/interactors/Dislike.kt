package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Completable

class Dislike(private val repository: ProductRepository) : Interactor<Completable> {
    var productId: Long = 0
    override fun build(): Completable {
        return repository.dislike(productId)
    }
}

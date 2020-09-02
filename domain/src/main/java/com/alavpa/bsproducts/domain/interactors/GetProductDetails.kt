package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Single

class GetProductDetails(private val repository: ProductRepository) : Interactor<Single<Product>> {
    var productId: Long = 0
    override fun build(): Single<Product> {
        return repository.getProductById(productId)
    }
}

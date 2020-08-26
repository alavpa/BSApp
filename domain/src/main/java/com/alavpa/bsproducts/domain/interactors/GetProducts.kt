package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Single

class GetProducts(private val repository: ProductRepository) : Interactor<Single<List<Product>>> {
    var page: Int = 1
    var size: Int = 5
    override fun build(): Single<List<Product>> {
        return repository.getProducts(page, size)
    }
}

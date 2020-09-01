package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Single

class GetProducts(private val repository: ProductRepository) : Interactor<Single<List<Product>>> {
    companion object {
        private const val PAGE_SIZE = 5
    }

    var page: Int = 1
    override fun build(): Single<List<Product>> {
        return repository.getProducts(page, PAGE_SIZE)
    }
}

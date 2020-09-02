package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.interactors.base.Interactor
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.reactivex.Single

class Likes(private val repository: ProductRepository) : Interactor<Single<List<Long>>> {
    override fun build(): Single<List<Long>> {
        return repository.likes()
    }
}

package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.ProductItemResponse
import io.reactivex.Single

class ApiDataSource(private val api: Api) : DataSource {
    override fun getItems(page: Int, size: Int): Single<List<ProductItemResponse>> {
        return api.getItems(page, size).map {
            it.body()?.list ?: listOf()
        }
    }
}

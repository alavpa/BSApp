package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.ErrorResponse
import com.alavpa.bsproducts.data.model.ProductDetailsResponse
import com.alavpa.bsproducts.data.model.ProductItemResponse
import com.alavpa.bsproducts.domain.error.ServerException
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.ResponseBody

class ApiDataSource(private val api: Api, private val gson: Gson) : DataSource {
    override fun getItems(page: Int, size: Int): Single<List<ProductItemResponse>> {
        return api.getItems(page, size).map {
            if (it.isSuccessful) {
                it.body()?.list ?: listOf()
            } else {
                val error = returnError(it.errorBody())
                throw ServerException(error.userMessage)
            }
        }
    }

    override fun getProductDetails(productId: Long): Single<ProductDetailsResponse> {
        return api.getProductDetails(productId).map {
            if (it.isSuccessful) {
                it.body()
            } else {
                val error = returnError(it.errorBody())
                throw ServerException(error.userMessage)
            }
        }
    }

    private fun returnError(errorBody: ResponseBody?): ErrorResponse {
        return gson.fromJson(
            errorBody?.string() ?: "{}",
            ErrorResponse::class.java
        )
    }
}

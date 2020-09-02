package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.PageResponse
import com.alavpa.bsproducts.data.model.ProductDetailsResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    companion object {
        const val BASE_URL = "https://bestsecret-recruitment-api.herokuapp.com/"
        const val TOKEN = "ddf49ca9-44cf-4613-b218-ddc030bbfa63"
    }

    @GET("products")
    fun getItems(
        @Query("page") page: Int,
        @Query("pageSize") size: Int,
        @Header("Authorization") token: String = TOKEN
    ): Single<Response<PageResponse>>

    @GET("products/{productId}")
    fun getProductDetails(
        @Path("productId") id: Long,
        @Header("Authorization") token: String = TOKEN
    ): Single<Response<ProductDetailsResponse>>
}

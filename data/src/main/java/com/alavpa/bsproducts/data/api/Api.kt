package com.alavpa.bsproducts.data.api

import com.alavpa.bsproducts.data.model.PageResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header

interface Api {

    companion object {
        const val BASE_URL = ""
        const val TOKEN = ""
    }

    @GET("products")
    fun getItems(
        @Field("page") page: Int,
        @Field("pageSize") size: Int,
        @Header("Authorization") token: String = TOKEN
    ): Single<Response<PageResponse>>
}

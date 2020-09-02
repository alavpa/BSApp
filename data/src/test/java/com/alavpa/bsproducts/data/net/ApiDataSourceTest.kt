package com.alavpa.bsproducts.data.net

import com.alavpa.bsproducts.data.api.Api
import com.alavpa.bsproducts.data.api.ApiDataSource
import com.alavpa.bsproducts.data.model.ErrorResponse
import com.alavpa.bsproducts.data.model.PageResponse
import com.alavpa.bsproducts.data.model.ProductDetailsResponse
import com.alavpa.bsproducts.data.model.ProductItemResponse
import com.alavpa.bsproducts.domain.error.ServerException
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ApiDataSourceTest {
    private lateinit var apiDataSource: ApiDataSource
    private val api: Api = mockk()

    @Before
    fun setup() {
        apiDataSource = ApiDataSource(api, Gson())
    }

    @Test
    fun `get items`() {
        every { api.getItems(any(), any()) } returns Single.just(
            Response.success(
                PageResponse(
                    list = listOf(
                        ProductItemResponse(
                            1,
                            "name",
                            "brand",
                            10,
                            "euro",
                            "image"
                        ),
                        ProductItemResponse(
                            2,
                            "name2",
                            "brand2",
                            20,
                            "euro2",
                            "image2"
                        )
                    ),
                    size = 2
                )
            )
        )

        apiDataSource.getItems(1, 2).test().also {
            it.assertValue(
                listOf(
                    ProductItemResponse(
                        1,
                        "name",
                        "brand",
                        10,
                        "euro",
                        "image"
                    ),
                    ProductItemResponse(
                        2,
                        "name2",
                        "brand2",
                        20,
                        "euro2",
                        "image2"
                    )
                )
            )
        }

        verify { api.getItems(1, 2, Api.TOKEN) }
    }

    @Test
    fun `get items server exception`() {

        val response = ErrorResponse(
            503,
            "internal",
            "user",
            "developer"
        )
        every { api.getItems(any(), any()) } returns Single.just(
            Response.error(
                503,
                ResponseBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(response)
                )

            )
        )

        apiDataSource.getItems(1, 2).test().also {
            it.assertError(ServerException("user"))
        }

        verify { api.getItems(1, 2, Api.TOKEN) }
    }

    @Test
    fun `get product details`() {
        every { api.getProductDetails(any()) } returns Single.just(
            Response.success(
                ProductDetailsResponse(
                    1,
                    "",
                    "",
                    "",
                    0,
                    "",
                    0,
                    "",
                    0
                )
            )
        )

        apiDataSource.getProductDetails(1).test().also {
            it.assertValue(
                ProductDetailsResponse(
                    1,
                    "",
                    "",
                    "",
                    0,
                    "",
                    0,
                    "",
                    0
                )
            )
        }

        verify { api.getProductDetails(1, Api.TOKEN) }
    }

    @Test
    fun `get details server exception`() {

        val response = ErrorResponse(
            503,
            "internal",
            "user",
            "developer"
        )
        every { api.getProductDetails(any(), any()) } returns Single.just(
            Response.error(
                503,
                ResponseBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    Gson().toJson(response)
                )

            )
        )

        apiDataSource.getProductDetails(1).test().also {
            it.assertError(ServerException("user"))
        }

        verify { api.getProductDetails(1, Api.TOKEN) }
    }
}

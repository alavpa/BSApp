package com.alavpa.bsproducts.data.net

import com.alavpa.bsproducts.data.api.Api
import com.alavpa.bsproducts.data.api.ApiDataSource
import com.alavpa.bsproducts.data.model.PageResponse
import com.alavpa.bsproducts.data.model.ProductItemResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ApiDataSourceTest {
    private lateinit var apiDataSource: ApiDataSource
    private val api: Api = mockk()

    @Before
    fun setup() {
        apiDataSource = ApiDataSource(api)
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
}

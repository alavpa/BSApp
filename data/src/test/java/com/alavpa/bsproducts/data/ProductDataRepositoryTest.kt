package com.alavpa.bsproducts.data

import com.alavpa.bsproducts.data.api.ApiDataSource
import com.alavpa.bsproducts.data.model.ProductItemResponse
import com.alavpa.bsproducts.domain.model.Product
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ProductDataRepositoryTest {
    private lateinit var repository: ProductDataRepository
    private val dataSource: ApiDataSource = mockk()

    @Before
    fun setup() {
        repository = ProductDataRepository(dataSource)
    }

    @Test
    fun `get products`() {
        every { dataSource.getItems(any(), any()) } returns Single.just(
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

        repository.getProducts(1, 2).test().also {
            it.assertValue(
                listOf(
                    Product(
                        1,
                        "name",
                        "",
                        "brand",
                        10,
                        "euro",
                        0,
                        "image",
                        0
                    ),
                    Product(
                        2,
                        "name2",
                        "",
                        "brand2",
                        20,
                        "euro2",
                        0,
                        "image2",
                        0
                    )
                )
            )
        }

        verify { dataSource.getItems(1, 2) }
    }
}

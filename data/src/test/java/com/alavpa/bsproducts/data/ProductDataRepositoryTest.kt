package com.alavpa.bsproducts.data

import com.alavpa.bsproducts.data.api.RemoteDataSource
import com.alavpa.bsproducts.data.local.LocalDataSource
import com.alavpa.bsproducts.data.model.ProductDetailsResponse
import com.alavpa.bsproducts.data.model.ProductItemResponse
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class ProductDataRepositoryTest {
    private lateinit var repository: ProductRepository
    private val remoteDataSource: RemoteDataSource = mockk()
    private val localDataSource: LocalDataSource = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = ProductDataRepository(remoteDataSource, localDataSource)
    }

    @Test
    fun `get products`() {
        every { remoteDataSource.getItems(any(), any()) } returns Single.just(
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

        verify { remoteDataSource.getItems(1, 2) }
    }

    @Test
    fun `get product details`() {
        every { remoteDataSource.getProductDetails(any()) } returns Single.just(
            ProductDetailsResponse(
                1,
                "name",
                "description",
                "brand",
                10,
                "euro",
                50,
                "image",
                5
            )
        )

        repository.getProductById(1).test().also {
            it.assertValue(
                Product(
                    1,
                    "name",
                    "description",
                    "brand",
                    10,
                    "euro",
                    50,
                    "image",
                    5
                )
            )
        }

        verify { remoteDataSource.getProductDetails(1) }
    }

    @Test
    fun like() {

        repository.like(1).test().also {
            it.assertComplete()
        }

        verify { localDataSource.like(1) }
    }

    @Test
    fun dislike() {

        repository.dislike(1).test().also {
            it.assertComplete()
        }

        verify { localDataSource.dislike(1) }
    }

    @Test
    fun likes() {

        every { localDataSource.likes() } returns listOf(1L)
        repository.likes().test().also {
            it.assertValue(listOf(1))
        }

        verify { localDataSource.likes() }
    }
}

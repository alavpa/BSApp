package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class GetProductDetailsTest {

    private val mockBuilder = ProductMockBuilder()
    private val repository: ProductRepository = mockk()
    lateinit var getProductDetails: GetProductDetails

    @Before
    fun setup() {
        getProductDetails = GetProductDetails(repository)
    }

    @Test
    fun `get product by id`() {

        val result = mockBuilder.id(1).build()
        every { repository.getProductById(any()) } returns Single.just(result)

        getProductDetails.productId = 1

        getProductDetails.build().test().also {
            it.assertValue(result)
            it.assertNoErrors()
        }

        verify { repository.getProductById(1) }
    }
}

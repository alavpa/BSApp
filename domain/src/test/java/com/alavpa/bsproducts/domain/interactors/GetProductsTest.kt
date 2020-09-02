package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class GetProductsTest {

    private val mockBuilder = ProductMockBuilder()
    private val repository: ProductRepository = mockk()
    lateinit var getProducts: GetProducts

    @Before
    fun setup() {
        getProducts = GetProducts(repository)
    }

    @Test
    fun `get products in page 1 size 5`() {

        val result = listOf(mockBuilder.id(1).build())
        every { repository.getProducts(any(), any()) } returns Single.just(result)

        getProducts.page = 1
        getProducts.build().test().also {
            it.assertValue(result)
            it.assertNoErrors()
        }

        verify { repository.getProducts(1, 5) }
    }
}

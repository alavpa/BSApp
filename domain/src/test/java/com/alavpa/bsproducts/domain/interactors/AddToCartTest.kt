package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.error.FeatureNotImplementedException
import com.alavpa.bsproducts.domain.error.NoStockException
import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class AddToCartTest {

    private val mockBuilder = ProductMockBuilder()
    private val repository: ProductRepository = mockk()
    private lateinit var addToCart: AddToCart

    @Before
    fun setup() {
        addToCart = AddToCart(repository)
    }

    @Test
    fun `add product to cart if stock`() {
        every { repository.getProductById(any()) } returns Single.just(
            mockBuilder.stock(5).build()
        )

        every { repository.addToCart(any()) } returns Completable.error(
            FeatureNotImplementedException()
        )

        addToCart.productId = 1

        addToCart.build().test().also {
            it.assertError(FeatureNotImplementedException::class.java)
        }

        verify { repository.getProductById(1) }
        verify { repository.addToCart(1) }
    }

    @Test
    fun `show error if not stock`() {
        every { repository.getProductById(any()) } returns Single.just(
            mockBuilder.stock(0).build()
        )

        addToCart.productId = 1

        addToCart.build().test().also {
            it.assertError(NoStockException::class.java)
        }

        verify { repository.getProductById(1) }
        verify { repository.addToCart(1) wasNot Called }
    }
}

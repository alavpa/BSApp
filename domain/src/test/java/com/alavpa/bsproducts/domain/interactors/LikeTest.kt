package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class LikeTest {

    private val mockBuilder = ProductMockBuilder()
    private val repository: ProductRepository = mockk()
    private lateinit var like: Like

    @Before
    fun setup() {
        like = Like(repository)
    }

    @Test
    fun `add product to like list`() {
        every { repository.getProductById(any()) } returns Single.just(
            mockBuilder.stock(5).build()
        )

        every { repository.like(any()) } returns Completable.complete()

        like.productId = 1

        like.build().test().also {
            it.assertComplete()
        }

        verify { repository.like(1) }
    }
}

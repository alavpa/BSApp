package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Before
import org.junit.Test

class DislikeTest {

    private val repository: ProductRepository = mockk()
    private lateinit var dislike: Dislike

    @Before
    fun setup() {
        dislike = Dislike(repository)
    }

    @Test
    fun `remove product to like list`() {
        every { repository.dislike(any()) } returns Completable.complete()

        dislike.productId = 1

        dislike.build().test().also {
            it.assertComplete()
        }

        verify { repository.dislike(1) }
    }
}

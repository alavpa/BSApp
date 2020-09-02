package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class LikesTest {

    private val repository: ProductRepository = mockk()
    private lateinit var likes: Likes

    @Before
    fun setup() {
        likes = Likes(repository)
    }

    @Test
    fun `getLikes`() {
        every { repository.likes() } returns Single.just(listOf(1))

        likes.build().test().also {
            it.assertValue(listOf(1))
        }

        verify { repository.likes() }
    }
}

package com.alavpa.bsproducts.presentation.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alavpa.bsproducts.domain.interactors.AddToCart
import com.alavpa.bsproducts.domain.interactors.GetProducts
import com.alavpa.bsproducts.presentation.ProductMockBuilder
import com.alavpa.bsproducts.presentation.di.testModule
import com.alavpa.bsproducts.presentation.utils.Navigation
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainPresenterTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val productMockBuilder = ProductMockBuilder()
    private val productItemMockBuilder = ProductItemMockBuilder()
    private val getProducts: GetProducts = mockk(relaxed = true)
    private val addToCart: AddToCart = mockk()
    private val navigation: Navigation = mockk(relaxed = true)
    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
        presenter = MainPresenter(getProducts, addToCart)
        presenter.attachNavigation(navigation)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `load items first page`() {

        every { getProducts.build() } returns Single.just(
            listOf(
                productMockBuilder.id(1).build(),
                productMockBuilder.id(2).build(),
                productMockBuilder.id(3).build()
            )
        )

        presenter.load()

        val viewModel = MainPresenter.ViewModel(
            items = listOf(
                productItemMockBuilder.id(1).build(),
                productItemMockBuilder.id(2).build(),
                productItemMockBuilder.id(3).build()
            )
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `get next page`() {
        presenter.renderLiveData.value = MainPresenter.ViewModel(
            items = listOf(
                productItemMockBuilder.id(1).build(),
                productItemMockBuilder.id(2).build(),
                productItemMockBuilder.id(3).build()
            )
        )
        every { getProducts.build() } returns Single.just(
            listOf(
                productMockBuilder.id(4).build(),
                productMockBuilder.id(5).build(),
                productMockBuilder.id(6).build()
            )
        )

        presenter.next()

        val viewModel = MainPresenter.ViewModel(
            items = listOf(
                productItemMockBuilder.id(1).build(),
                productItemMockBuilder.id(2).build(),
                productItemMockBuilder.id(3).build(),
                productItemMockBuilder.id(4).build(),
                productItemMockBuilder.id(5).build(),
                productItemMockBuilder.id(6).build()
            )
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on click on item`() {
        presenter.clickOn(productItemMockBuilder.id(1).build())
        verify { navigation.goToProductDetails(1) }
    }
}

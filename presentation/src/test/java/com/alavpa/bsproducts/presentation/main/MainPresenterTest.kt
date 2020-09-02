package com.alavpa.bsproducts.presentation.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alavpa.bsproducts.domain.error.ServerException
import com.alavpa.bsproducts.domain.interactors.GetProducts
import com.alavpa.bsproducts.presentation.ProductItemMockBuilder
import com.alavpa.bsproducts.presentation.ProductMockBuilder
import com.alavpa.bsproducts.presentation.di.testModule
import com.alavpa.bsproducts.presentation.utils.Navigation
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.ArgumentCaptor

class MainPresenterTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val productMockBuilder = ProductMockBuilder()
    private val productItemMockBuilder = ProductItemMockBuilder()
    private val getProducts: GetProducts = mock()
    private val navigation: Navigation = mock()
    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
        presenter = MainPresenter(getProducts)
        presenter.attachNavigation(navigation)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `load items first page`() {

        val mockObserver: Observer<MainPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(getProducts.build()).willReturn(
            Single.just(
                listOf(
                    productMockBuilder.id(1).build(),
                    productMockBuilder.id(2).build(),
                    productMockBuilder.id(3).build()
                )
            )
        )

        presenter.load()

        val captor = ArgumentCaptor.forClass(MainPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = MainPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = MainPresenter.ViewModel(
            items = listOf(
                productItemMockBuilder.id(1).build(),
                productItemMockBuilder.id(2).build(),
                productItemMockBuilder.id(3).build()
            )
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
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
        given(getProducts.build()).willReturn(
            Single.just(
                listOf(
                    productMockBuilder.id(4).build(),
                    productMockBuilder.id(5).build(),
                    productMockBuilder.id(6).build()
                )
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
        verify(navigation).goToProductDetails(1)
    }

    @Test
    fun `on load show server error`() {
        given(getProducts.build()).willReturn(Single.error(ServerException("user")))
        presenter.load()

        val viewModel = MainPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on next show server error`() {
        given(getProducts.build()).willReturn(Single.error(ServerException("user")))
        presenter.next()

        val viewModel = MainPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on load show unknown error`() {
        given(getProducts.build()).willReturn(Single.error(Throwable()))
        presenter.load()

        val viewModel = MainPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on next show unknwon error`() {
        given(getProducts.build()).willReturn(Single.error(Throwable()))
        presenter.next()

        val viewModel = MainPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }
}

package com.alavpa.bsproducts.presentation.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.alavpa.bsproducts.domain.error.FeatureNotImplementedException
import com.alavpa.bsproducts.domain.error.NoStockException
import com.alavpa.bsproducts.domain.error.ServerException
import com.alavpa.bsproducts.domain.interactors.AddToCart
import com.alavpa.bsproducts.domain.interactors.GetProductDetails
import com.alavpa.bsproducts.presentation.ProductMockBuilder
import com.alavpa.bsproducts.presentation.di.testModule
import com.alavpa.bsproducts.presentation.utils.Navigation
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class DetailsPresenterTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val productMockBuilder = ProductMockBuilder()
    private val getProductDetails: GetProductDetails = mock()
    private val addToCart: AddToCart = mock()
    private val navigation: Navigation = mock()
    private lateinit var presenter: DetailsPresenter

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
        presenter = DetailsPresenter(getProductDetails, addToCart)
        presenter.attachNavigation(navigation)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `load item`() {

        given(getProductDetails.build()).willReturn(
            Single.just(
                productMockBuilder.id(1)
                    .name("title")
                    .brand("brand")
                    .image("image")
                    .description("description")
                    .price(20)
                    .discount(50)
                    .currency("$")
                    .build()
            )
        )

        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            productId = 1,
            title = "title",
            brand = "brand",
            isLoading = false,
            image = "image",
            description = "description",
            price = "20$",
            priceWithDiscount = "10$"
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart item with no stock`() {
        given(addToCart.build()).willReturn(Completable.error(NoStockException()))
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showNoStockError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart item with feature not implemented`() {
        given(addToCart.build()).willReturn(Completable.error(FeatureNotImplementedException()))
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showFeatureNotImplementedError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart item with unknown error`() {
        given(addToCart.build()).willReturn(Completable.error(Exception()))
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on load product server error`() {
        given(getProductDetails.build()).willReturn(Single.error(ServerException("user")))
        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on load product unknown error`() {
        given(getProductDetails.build()).willReturn(Single.error(Throwable()))
        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart unknown error`() {
        given(getProductDetails.build()).willReturn(Single.error(Throwable()))

        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }
}

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
import io.mockk.every
import io.mockk.mockk
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
    private val getProductDetails: GetProductDetails = mockk(relaxed = true)
    private val addToCart: AddToCart = mockk(relaxed = true)
    private val navigation: Navigation = mockk(relaxed = true)
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

        every { getProductDetails.build() } returns Single.just(
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
        every { addToCart.build() } returns Completable.error(NoStockException())
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showNoStockError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart item with feature not implemented`() {
        every { addToCart.build() } returns Completable.error(FeatureNotImplementedException())
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showFeatureNotImplementedError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart item with unknown error`() {
        every { addToCart.build() } returns Completable.error(Exception())
        presenter.onAddToCart()

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on load product server error`() {
        every { getProductDetails.build() } returns Single.error(ServerException("user"))
        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on load product unknown error`() {
        every { getProductDetails.build() } returns Single.error(Throwable())
        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }

    @Test
    fun `on add to cart unknown error`() {
        every { getProductDetails.build() } returns Single.error(Throwable())
        presenter.load(1)

        val viewModel = DetailsPresenter.ViewModel(
            showUnknownError = true
        )
        assertEquals(viewModel, presenter.renderLiveData.value)
    }
}

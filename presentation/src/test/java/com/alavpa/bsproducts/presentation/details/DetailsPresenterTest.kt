package com.alavpa.bsproducts.presentation.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alavpa.bsproducts.domain.error.FeatureNotImplementedException
import com.alavpa.bsproducts.domain.error.NoStockException
import com.alavpa.bsproducts.domain.error.ServerException
import com.alavpa.bsproducts.domain.interactors.*
import com.alavpa.bsproducts.presentation.ProductMockBuilder
import com.alavpa.bsproducts.presentation.di.testModule
import com.alavpa.bsproducts.presentation.utils.Navigation
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.ArgumentCaptor

class DetailsPresenterTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val productMockBuilder = ProductMockBuilder()
    private val getProductDetails: GetProductDetails = mock()
    private val likes: Likes = mock()
    private val like: Like = mock()
    private val dislike: Dislike = mock()
    private val addToCart: AddToCart = mock()
    private val navigation: Navigation = mock()
    private lateinit var presenter: DetailsPresenter

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
        presenter = DetailsPresenter(getProductDetails, addToCart, likes, like, dislike)
        presenter.attachNavigation(navigation)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `load item not liked`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(likes.build()).willReturn(Single.just(listOf(7)))

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

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            productId = 1,
            title = "title",
            brand = "brand",
            isLoading = false,
            image = "image",
            description = "description",
            price = "20$",
            priceWithDiscount = "10$",
            liked = false
        )
        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `load item liked`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(likes.build()).willReturn(Single.just(listOf(1)))

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

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            productId = 1,
            title = "title",
            brand = "brand",
            isLoading = false,
            image = "image",
            description = "description",
            price = "20$",
            priceWithDiscount = "10$",
            liked = true
        )
        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on add to cart item with no stock`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(addToCart.build()).willReturn(Completable.error(NoStockException()))
        presenter.onAddToCart()

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showNoStockError = true
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on add to cart item with feature not implemented`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(addToCart.build()).willReturn(Completable.error(FeatureNotImplementedException()))
        presenter.onAddToCart()

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showFeatureNotImplementedError = true
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on add to cart item with unknown error`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(addToCart.build()).willReturn(Completable.error(Exception()))
        presenter.onAddToCart()

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on load product server error`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(getProductDetails.build()).willReturn(Single.error(ServerException("user")))
        presenter.load(1)

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showServerException = Pair(true, "user")
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on load product unknown error`() {
        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(getProductDetails.build()).willReturn(Single.error(Throwable()))
        presenter.load(1)

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on add to cart unknown error`() {

        val mockObserver: Observer<DetailsPresenter.ViewModel> = mock()
        presenter.renderLiveData.observeForever(mockObserver)

        given(getProductDetails.build()).willReturn(Single.error(Throwable()))

        presenter.load(1)

        val captor = ArgumentCaptor.forClass(DetailsPresenter.ViewModel::class.java)
        verify(mockObserver, times(2)).onChanged(capture(captor))

        val states = captor.allValues

        val viewModel1 = DetailsPresenter.ViewModel(
            isLoading = true
        )

        val viewModel2 = DetailsPresenter.ViewModel(
            showUnknownError = true
        )

        assertEquals(viewModel1, states[0])
        assertEquals(viewModel2, states[1])
    }

    @Test
    fun `on click menu like when is liked`() {

        given(dislike.build()).willReturn(Completable.complete())
        given(getProductDetails.build()).willReturn(
            Single.just(productMockBuilder.id(1).build())
        )
        given(likes.build()).willReturn(Single.just(listOf()))

        presenter.renderLiveData.value = DetailsPresenter.ViewModel(liked = true)
        presenter.onClickLike()

        verify(dislike).build()
        verify(getProductDetails).build()
    }

    @Test
    fun `on click menu like when is not liked`() {

        given(like.build()).willReturn(Completable.complete())
        given(getProductDetails.build()).willReturn(
            Single.just(productMockBuilder.id(1).build())
        )
        given(likes.build()).willReturn(Single.just(listOf()))

        presenter.renderLiveData.value = DetailsPresenter.ViewModel(liked = false)
        presenter.onClickLike()

        verify(like).build()
        verify(getProductDetails).build()
    }
}

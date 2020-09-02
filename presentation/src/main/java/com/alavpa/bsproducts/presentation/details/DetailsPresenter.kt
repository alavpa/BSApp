package com.alavpa.bsproducts.presentation.details

import androidx.lifecycle.MutableLiveData
import com.alavpa.bsproducts.domain.error.FeatureNotImplementedException
import com.alavpa.bsproducts.domain.error.NoStockException
import com.alavpa.bsproducts.domain.error.ServerException
import com.alavpa.bsproducts.domain.interactors.*
import com.alavpa.bsproducts.domain.model.Product
import com.alavpa.bsproducts.presentation.BasePresenter
import com.alavpa.bsproducts.presentation.utils.Navigation

class DetailsPresenter(
    private val getProductDetails: GetProductDetails,
    private val addToCart: AddToCart,
    private val likes: Likes,
    private val like: Like,
    private val dislike: Dislike,
) : BasePresenter() {

    companion object {
        private const val PERCENT = 100.0
    }

    val renderLiveData = MutableLiveData<ViewModel>()
    private val viewModel: ViewModel
        get() = renderLiveData.value ?: ViewModel()

    private var navigation: Navigation? = null
    fun attachNavigation(navigation: Navigation) {
        this.navigation = navigation
    }

    fun detachNavigation() {
        this.navigation = null
    }

    fun load(productId: Long) {
        renderLiveData.value = viewModel.copy(isLoading = true)
        getProductDetails.productId = productId
        getProductDetails.build().exec(::renderError) { product ->
            likes.build().exec { likesList ->
                renderProduct(product, likesList.contains(product.id))
            }

        }
    }

    private fun renderError(throwable: Throwable) {
        when (throwable) {
            is FeatureNotImplementedException -> renderLiveData.value = viewModel.copy(
                showFeatureNotImplementedError = true,
                isLoading = false
            )
            is NoStockException ->
                renderLiveData.value = viewModel.copy(showNoStockError = true, isLoading = false)
            is ServerException ->
                renderLiveData.value = viewModel.copy(
                    showServerException = Pair(true, throwable.userMessage),
                    isLoading = false
                )
            else ->
                renderLiveData.value = viewModel.copy(showUnknownError = true, isLoading = false)
        }
    }

    fun onAddToCart() {
        renderLiveData.value = viewModel.copy(isLoading = true)
        addToCart.productId = viewModel.productId
        addToCart.build().exec(::renderError) {
            renderLiveData.value = viewModel.copy(isLoading = false, productAddedToCart = true)
        }
    }

    private fun renderProduct(product: Product, liked: Boolean) {
        renderLiveData.value = viewModel.copy(
            productId = product.id,
            title = product.name,
            image = product.image,
            brand = product.brand,
            description = product.description,
            price = "${product.price}${product.currency}",
            priceWithDiscount = "${calculatePriceWithDiscount(product)}${product.currency}",
            isLoading = false,
            liked = liked
        )
    }

    private fun calculatePriceWithDiscount(product: Product): Int {
        return product.price - (product.discountPercentage / PERCENT * product.price).toInt()
    }

    fun close() {
        navigation?.close()
    }

    fun onNotify() {
        renderLiveData.value = viewModel.copy(
            showNoStockError = false,
            showFeatureNotImplementedError = true
        )
    }

    fun onCancelNoStockDialog() {
        renderLiveData.value = viewModel.copy(
            showNoStockError = false
        )
    }

    fun onCloseFeatureNotImplementedDialog() {
        renderLiveData.value = viewModel.copy(
            showFeatureNotImplementedError = false
        )
    }

    fun onCloseServerException() {
        renderLiveData.value = viewModel.copy(showServerException = Pair(false, ""))
    }

    fun onCloseUnknownError() {
        renderLiveData.value = viewModel.copy(showUnknownError = false)
    }

    fun onClickLike() {
        if (viewModel.liked) {
            dislike.productId = viewModel.productId
            dislike.build().exec {
                load(viewModel.productId)
            }
        } else {
            like.productId = viewModel.productId
            like.build().exec {
                load(viewModel.productId)
            }
        }
    }

    data class ViewModel(
        val isLoading: Boolean = false,
        val productId: Long = 0,
        val image: String = "",
        val title: String = "",
        val brand: String = "",
        val description: String = "",
        val price: String = "",
        val priceWithDiscount: String = "",
        val liked: Boolean = false,
        val showNoStockError: Boolean = false,
        val showFeatureNotImplementedError: Boolean = false,
        val showUnknownError: Boolean = false,
        val productAddedToCart: Boolean = false,
        val showServerException: Pair<Boolean, String> = Pair(false, "")
    )
}

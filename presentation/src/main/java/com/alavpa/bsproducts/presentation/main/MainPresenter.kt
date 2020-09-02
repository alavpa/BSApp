package com.alavpa.bsproducts.presentation.main

import androidx.lifecycle.MutableLiveData
import com.alavpa.bsproducts.domain.error.ServerException
import com.alavpa.bsproducts.domain.interactors.GetProducts
import com.alavpa.bsproducts.presentation.BasePresenter
import com.alavpa.bsproducts.presentation.model.ProductItem
import com.alavpa.bsproducts.presentation.model.toItem
import com.alavpa.bsproducts.presentation.utils.Navigation

class MainPresenter(
    private val getProducts: GetProducts
) : BasePresenter() {

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


    fun load() {
        renderLiveData.value = viewModel.copy(isLoading = true)
        getProducts.page = 1
        getProducts.build().exec(::renderError) { products ->
            renderLiveData.value = viewModel.copy(
                items = products.map { it.toItem() },
                isLoading = false
            )
        }
    }

    fun next() {
        if (!viewModel.isLoading) {
            renderLiveData.value = viewModel.copy(isLoading = true)
            getProducts.page = getProducts.page + 1
            getProducts.build().exec(::renderError) { products ->
                renderLiveData.value = viewModel.copy(
                    items = viewModel.items.toMutableList()
                        .apply { addAll(products.map { it.toItem() }) },
                    isLoading = false
                )
            }
        }
    }

    private fun renderError(throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is ServerException -> {
                renderLiveData.value = viewModel.copy(
                    isLoading = false,
                    showServerException = Pair(true, throwable.userMessage)
                )
            }
            else -> renderLiveData.value = viewModel.copy(
                isLoading = false,
                showUnknownError = true
            )
        }
    }

    fun onCloseServerException() {
        renderLiveData.value = viewModel.copy(showServerException = Pair(false, ""))
    }

    fun clickOn(item: ProductItem) {
        navigation?.goToProductDetails(item.id)
    }

    fun onCloseUnknownError() {
        renderLiveData.value = viewModel.copy(showUnknownError = false)
    }

    data class ViewModel(
        val isLoading: Boolean = false,
        val items: List<ProductItem> = listOf(),
        val showServerException: Pair<Boolean, String> = Pair(false, ""),
        val showUnknownError: Boolean = false
    )
}

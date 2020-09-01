package com.alavpa.bsproducts.presentation.main

import androidx.lifecycle.MutableLiveData
import com.alavpa.bsproducts.domain.interactors.AddToCart
import com.alavpa.bsproducts.domain.interactors.GetProducts
import com.alavpa.bsproducts.presentation.BasePresenter
import com.alavpa.bsproducts.presentation.model.ProductItem
import com.alavpa.bsproducts.presentation.model.toItem
import com.alavpa.bsproducts.presentation.utils.Navigation

class MainPresenter(
    private val getProducts: GetProducts,
    private val addToCart: AddToCart
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
        getProducts.build().exec { products ->
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
            getProducts.build().exec { products ->
                renderLiveData.value = viewModel.copy(
                    items = viewModel.items.toMutableList()
                        .apply { addAll(products.map { it.toItem() }) },
                    isLoading = false
                )
            }
        }
    }

    fun clickOn(item: ProductItem) {
        navigation?.goToProductDetails(item.id)
    }

    data class ViewModel(
        val isLoading: Boolean = false,
        val items: List<ProductItem> = listOf()
    )
}

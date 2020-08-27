package com.alavpa.bsproducts.domain.di

import com.alavpa.bsproducts.domain.interactors.AddToCart
import com.alavpa.bsproducts.domain.interactors.GetProductDetails
import com.alavpa.bsproducts.domain.interactors.GetProducts
import org.koin.dsl.module

val domainModule = module {
    factory { GetProducts(get()) }
    factory { GetProductDetails(get()) }
    factory { AddToCart(get()) }
}

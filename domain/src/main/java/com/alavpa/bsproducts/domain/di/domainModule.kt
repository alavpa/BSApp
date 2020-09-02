package com.alavpa.bsproducts.domain.di

import com.alavpa.bsproducts.domain.interactors.*
import org.koin.dsl.module

val domainModule = module {
    factory { GetProducts(get()) }
    factory { GetProductDetails(get()) }
    factory { AddToCart(get()) }
    factory { Like(get()) }
    factory { Dislike(get()) }
    factory { Likes(get()) }
}

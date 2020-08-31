package com.alavpa.bsproducts.di

import com.alavpa.bsproducts.utils.loader.GlideImageLoader
import com.alavpa.bsproducts.utils.loader.ImageLoader
import org.koin.dsl.bind
import org.koin.dsl.module

val androidModule = module {
    single { GlideImageLoader() } bind ImageLoader::class
}

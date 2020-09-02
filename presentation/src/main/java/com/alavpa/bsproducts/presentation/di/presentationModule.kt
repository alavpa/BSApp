package com.alavpa.bsproducts.presentation.di

import com.alavpa.bsproducts.presentation.details.DetailsPresenter
import com.alavpa.bsproducts.presentation.main.MainPresenter
import com.alavpa.bsproducts.presentation.utils.InteractorExecutor
import com.alavpa.bsproducts.presentation.utils.ThreadProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single { ThreadProvider() }
    factory { InteractorExecutor(get()) }
    viewModel { MainPresenter(get()) }
    viewModel { DetailsPresenter(get(), get()) }
}

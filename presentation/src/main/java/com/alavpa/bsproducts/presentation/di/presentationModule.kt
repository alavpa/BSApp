package com.alavpa.bsproducts.presentation.di

import com.alavpa.bsproducts.presentation.utils.InteractorExecutor
import com.alavpa.bsproducts.presentation.utils.ThreadProvider
import org.koin.dsl.module

val presentationModule = module {
    single { ThreadProvider() }
    factory { InteractorExecutor(get()) }
}

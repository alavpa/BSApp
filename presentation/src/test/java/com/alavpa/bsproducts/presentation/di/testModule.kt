package com.alavpa.bsproducts.presentation.di

import com.alavpa.bsproducts.presentation.utils.InteractorExecutor
import com.alavpa.bsproducts.presentation.utils.ThreadProvider
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module

val testModule = module(override = true) {
    single { threadProvider() }
    factory { InteractorExecutor(get()) }
}

fun threadProvider(): ThreadProvider {
    return mock<ThreadProvider>().apply {
        given(provideIO()).willReturn(Schedulers.trampoline())
        given(provideMain()).willReturn(Schedulers.trampoline())
    }
}

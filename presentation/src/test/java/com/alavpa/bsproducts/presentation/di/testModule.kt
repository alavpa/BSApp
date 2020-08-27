package com.alavpa.bsproducts.presentation.di

import com.alavpa.bsproducts.presentation.utils.InteractorExecutor
import com.alavpa.bsproducts.presentation.utils.ThreadProvider
import io.mockk.every
import io.mockk.mockk
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module

val testModule = module(override = true) {
    single { threadProvider() }
    factory { InteractorExecutor(get()) }
}

fun threadProvider(): ThreadProvider {
    return mockk<ThreadProvider>().apply {
        every { provideIO() } returns Schedulers.trampoline()
        every { provideMain() } returns Schedulers.trampoline()
    }
}

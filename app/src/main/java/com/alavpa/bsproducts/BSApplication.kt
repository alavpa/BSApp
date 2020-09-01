package com.alavpa.bsproducts

import androidx.multidex.MultiDexApplication
import com.alavpa.bsproducts.data.di.dataModule
import com.alavpa.bsproducts.di.androidModule
import com.alavpa.bsproducts.domain.di.domainModule
import com.alavpa.bsproducts.presentation.di.presentationModule
import org.koin.core.context.startKoin

class BSApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                listOf(
                    dataModule,
                    domainModule,
                    presentationModule,
                    androidModule
                )
            )
        }
    }
}

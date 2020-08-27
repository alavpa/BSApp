package com.alavpa.bsproducts.presentation.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ThreadProvider {
    fun provideMain(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    fun provideIO(): Scheduler {
        return Schedulers.io()
    }
}

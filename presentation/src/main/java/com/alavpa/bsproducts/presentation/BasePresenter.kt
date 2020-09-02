package com.alavpa.bsproducts.presentation

import androidx.lifecycle.ViewModel
import com.alavpa.bsproducts.presentation.utils.InteractorExecutor
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

open class BasePresenter : KoinComponent, ViewModel() {

    private val interactorExecutor: InteractorExecutor by inject()

    protected fun <T> Single<T>.exec(
        onError: (Throwable) -> Unit = { Timber.e(it) },
        onSuccess: (T) -> Unit
    ) {
        interactorExecutor.exec(this, onSuccess, onError)
    }

    protected fun Completable.exec(
        onError: (Throwable) -> Unit = { Timber.e(it) },
        onSuccess: () -> Unit
    ) {
        interactorExecutor.exec(this, onSuccess, onError)
    }

    fun destroy() {
        interactorExecutor.clear()
    }
}

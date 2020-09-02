package com.alavpa.bsproducts.presentation.utils

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class InteractorExecutor(private val threadProvider: ThreadProvider) {

    private val disposables = CompositeDisposable()

    fun <T> exec(
        single: Single<T>,
        onSuccess: (T) -> Unit,
        onException: (Throwable) -> Unit = { Timber.e(it) }
    ) {
        single.subscribeOn(threadProvider.provideIO())
            .observeOn(threadProvider.provideMain())
            .subscribe(onSuccess, onException)
            .also { disposables.add(it) }
    }

    fun exec(
        completable: Completable,
        onSuccess: () -> Unit,
        onException: (Throwable) -> Unit = { Timber.e(it) }
    ) {
        completable.subscribeOn(threadProvider.provideIO())
            .observeOn(threadProvider.provideMain())
            .subscribe(onSuccess, onException)
            .also { disposables.add(it) }
    }

    fun clear() {
        disposables.clear()
    }
}

package com.alavpa.bsproducts.domain.interactors.base

interface Interactor<T> {
    fun build(): T
}

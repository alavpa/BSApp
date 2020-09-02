package com.alavpa.bsproducts.data.local

interface LocalDataSource {

    fun like(id: Long)
    fun dislike(id: Long)
    fun likes(): List<Long>
}

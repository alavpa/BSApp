package com.alavpa.bsproducts.domain.error

data class ServerException(val userMessage: String) : Exception(userMessage)

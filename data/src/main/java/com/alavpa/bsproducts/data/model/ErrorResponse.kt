package com.alavpa.bsproducts.data.model

data class ErrorResponse(
    val httpStatusCode: Int,
    val internalErrorCode: String,
    val userMessage: String,
    val developerMessage: String
)

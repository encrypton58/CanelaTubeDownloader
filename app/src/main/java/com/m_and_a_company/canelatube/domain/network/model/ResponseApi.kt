package com.m_and_a_company.canelatube.domain.network.model

sealed class ResponseApi<out T>(
    val statusCode: Int,
    val data: T? = null,
    val message: String? = null,
    val warningMessage: String? = null,
    val errors: List<ErrorModel>? = null
) {

    data class Success<out T>(
        val _statusCode: Int,
        val _data: T? = null,
        val _message: String? = null,
        val _warningMessage: String? = null
    ) : ResponseApi<T>(
        _statusCode,
        _data,
        _message,
        _warningMessage
    )

    data class Error(
        val _statusCode: Int,
        val _message: String? = null,
        val _errors: List<ErrorModel>? = null
    ) : ResponseApi<Nothing>(
        _statusCode,
        null,
        _message,
        null,
        _errors
    )

}
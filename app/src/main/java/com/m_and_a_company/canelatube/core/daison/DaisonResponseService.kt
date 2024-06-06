package com.m_and_a_company.canelatube.core.daison

import com.m_and_a_company.canelatube.domain.network.model.ErrorModel

sealed class DaisonResponseService<out T> {

    data class Success<out T>(val data: T): DaisonResponseService<T>()

    data class Error(
        val statusCode: Int,
        val message: String,
        val errors: List<ErrorModel>?
    ): DaisonResponseService<Nothing>()

}

data class ResultSet<T>(
    val data: T,
    val statusCode: Int,
    val message: String,
    val warningMessage: String? = null,
)
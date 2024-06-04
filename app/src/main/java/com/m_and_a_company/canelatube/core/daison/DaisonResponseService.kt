package com.m_and_a_company.canelatube.core.daison

sealed class DaisonResponseService<out T> {

    data class Success<out T>(val data: T): DaisonResponseService<T>()

    data class Error(
        val statusCode: Int,
        val message: String,
        val errors: List<String>?
    ): DaisonResponseService<Nothing>()

}

data class ResultSet<T>(
    val resultSet: T,
    val statusCode: Int,
    val message: String
)
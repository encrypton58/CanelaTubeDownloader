package com.m_and_a_company.canelatube.domain.network.exceptions

import com.m_and_a_company.canelatube.domain.network.enum.TypeError
import com.m_and_a_company.canelatube.domain.network.model.ErrorModel

data class SongException(
    private val messageOfError: String,
    private val errors: List<ErrorModel>? = emptyList(),
    private val statusCode: Int? = null,
    private val typeError: TypeError? = TypeError.UNDEFINED
    ): Exception(messageOfError) {

    fun getErrors(): List<ErrorModel>? = errors
    fun getTypeError(): TypeError? = typeError

}
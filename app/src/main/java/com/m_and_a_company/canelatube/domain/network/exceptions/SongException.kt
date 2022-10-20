package com.m_and_a_company.canelatube.domain.network.exceptions

import com.m_and_a_company.canelatube.network.domain.model.ErrorModel

data class SongException(
    private val messageOfError: String,
    private val errors: List<ErrorModel>? = emptyList()
    ): Exception(messageOfError) {

        fun getErrors(): List<ErrorModel>? = errors


}
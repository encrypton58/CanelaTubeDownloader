package com.m_and_a_company.canelatube.domain.network.model

import com.m_and_a_company.canelatube.domain.data.models.REQUEST_FROM_CONVERT_TYPE

data class ResponseService(
    val code: Int,
    val data: Any,
    val toParse: String,
    val errors : RequestErrors = RequestErrors(arrayListOf())
) {
    override fun toString() = REQUEST_FROM_CONVERT_TYPE
}
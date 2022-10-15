package com.m_and_a_company.canelatube.domain.network.model

import com.m_and_a_company.canelatube.domain.data.models.REQUEST_ERRORS_TYPE

data class RequestErrors(
    val errors: List<RequestParamError>?,
    val statusCode: Int = 0,
    val message: String = ""
){
    override fun toString(): String {
        return REQUEST_ERRORS_TYPE
    }
}
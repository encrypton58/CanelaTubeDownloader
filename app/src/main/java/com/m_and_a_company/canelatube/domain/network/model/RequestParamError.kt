package com.m_and_a_company.canelatube.domain.network.model

data class RequestParamError(
    val rule: String,
    val field: String,
    val message: String
)
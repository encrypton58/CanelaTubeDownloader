package com.m_and_a_company.canelatube.domain.network.model

data class ErrorModel(
    val rule: String? = null,
    val field: String? = null,
    val message: String? = null
)

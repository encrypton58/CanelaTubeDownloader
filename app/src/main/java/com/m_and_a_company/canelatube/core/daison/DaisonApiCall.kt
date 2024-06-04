package com.m_and_a_company.canelatube.core.daison

import retrofit2.Response


interface DaisonApiCall {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): DaisonResponseService<T>

}
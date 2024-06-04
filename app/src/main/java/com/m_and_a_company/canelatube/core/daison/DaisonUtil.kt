package com.m_and_a_company.canelatube.core.daison

object DaisonUtil {

    const val STATUS_CODE_NO_INTERNET = 8

    fun getMessageFromStatusCode(statusCode: Int): String {
        return when (statusCode) {
            STATUS_CODE_NO_INTERNET -> "No internet connection"
            else -> "Something went wrong"
        }
    }

}
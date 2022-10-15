package com.m_and_a_company.canelatube.network.domain.enum

enum class ResponseStatus(val type: Int) {

    SUCCESS(0),
    ERROR_CODE(1),
    ERROR_CLIENT(2),
    ERROR_DB(3);

    companion object {
        fun fromInt(value: Int) = values().first(){ it.type == value }
    }

}
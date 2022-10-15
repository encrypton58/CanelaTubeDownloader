package com.m_and_a_company.canelatube.ui.enums

enum class TypeDownload {
    AUDIO {
        override fun getType(): String {
            return "song"
        }
    },
    VIDEO {
        override fun getType(): String {
            return "video"
        }
    },
    UNDEFINED {
        override fun getType(): String {
            return "undefined"
        }
    };

    abstract fun getType(): String

}
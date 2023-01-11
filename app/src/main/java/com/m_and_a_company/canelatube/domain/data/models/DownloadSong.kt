package com.m_and_a_company.canelatube.domain.data.models

data class DownloadSong(
    val id: Int,
    val requiredDelete: Boolean,
    val requiredGetName: Boolean = false)

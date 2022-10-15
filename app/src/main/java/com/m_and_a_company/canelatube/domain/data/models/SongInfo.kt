package com.m_and_a_company.canelatube.domain.data.models

import com.google.gson.annotations.SerializedName

data class SongInfo(
    val title: String,
    val formats: List<Format>,
    val thumbnail: String
){
    override fun toString() = SONG_INFO_TYPE
}

data class Format(
    val abr: String,
    val format: String,
    val size: String,
    val itag: Int
)
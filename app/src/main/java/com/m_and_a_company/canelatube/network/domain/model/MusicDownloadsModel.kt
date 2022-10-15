package com.m_and_a_company.canelatube.network.domain.model

data class MusicDownloadsModel(
    val title: String = "",
    val author: String = "",
    val formats: List<Format> = emptyList(),
    val thumbnail: String = ""
)


data class Format(
    val itag: Int = 0,
    val type: String = "",
    val size: String = "",
    val quality: String = ""
)

data class SongIdModel(
    val id: Int = 0,
    val name: String = "",
    val ext: String = ""
)

data class SongDeleteModel(
    val isDelete: Boolean = false
 )
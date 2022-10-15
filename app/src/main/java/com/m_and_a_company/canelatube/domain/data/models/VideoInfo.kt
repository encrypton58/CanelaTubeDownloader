package com.m_and_a_company.canelatube.domain.data.models

data class VideoInfo(
    val title: String,
    val formats: List<VideoFormat>,
    val thumbnail: String,
) {
    override fun toString() = VIDEO_INFO_TYPE
}

data class VideoFormat(
    val res: String,
    val format: String,
    val size: String,
    val itag: Int
)
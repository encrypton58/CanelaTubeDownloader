package com.m_and_a_company.canelatube.domain.data.models

import android.graphics.Bitmap
import android.net.Uri

data class SongDownloaded(
    val id: Long,
    val title: String,
    val artist: String,
    val imageAlbumArt: Bitmap?,
    val uri: Uri,
    val path: String
)

package com.m_and_a_company.canelatube.environment

import android.os.Build
import android.os.Environment
import com.m_and_a_company.canelatube.domain.data.models.DIRECTORY_NAME_SAVE_SONGS
import java.io.File

const val PATH_SEARCH_lLOCAL_SONGS = "/storage/emulated/0/Music/canelaTube/%"

fun getPathSongs(nameFile: String): File {
    val path = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        DIRECTORY_NAME_SAVE_SONGS
    )
    val exists = path.exists()
    return if (!exists) {
        path.mkdirs()
        File(path, nameFile)
    } else {
        File(path, nameFile)
    }
}


fun isUpApi29(): Boolean{
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}
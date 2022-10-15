package com.m_and_a_company.canelatube.enviroment

import android.os.Environment
import com.m_and_a_company.canelatube.domain.data.models.DIRECTORY_NAME_SAVE_SONGS
import java.io.File

const val VERSION_R_MORE = "R+"
const val VERSION_LESS_R = "R-"
const val ID_NOTIFICATION_DOWNLOAD = 1009
const val ID_NOTIFICATION_DOWNLOAD_SUCCESS = 1010
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
package com.m_and_a_company.canelatube.domain.network.model

/**
 * Modelo de respuesta al momento de consultar la información de una canción
 * atravez de su url
 * @param title Título de la canción
 * @param thumbnail Imagen de la canción
 * @param formats Formatos de descarga de la canción
 */
data class MusicDownloadsModel(
    val title: String = "",
    val author: String = "",
    val formats: List<Format> = emptyList(),
    val thumbnail: String = "",
    val warningMessage: String? = ""
)

/**
 *
 * {
 *     "itag": 251,
 *     "type": "MP3",
 *     "quality": "160kbps",
 *     "size": "3.5 MB"
 *  }
 *
 * Maneja los datos de un formato de descarga
 * @param itag numero que identifica el formato de descarga
 * @param type tipo de formato de descarga
 * @param quality calidad del formato de descarga
 * @param size tamaño del formato de descarga
 */
data class Format(
    val itag: Int = 0,
    val type: String = "",
    val size: String = "",
    val quality: String = ""
)

/**
 * Maneja cuando se efectua una conversion para su posterior descarga
 * @param id titulo de la cancion
 * @param name autor de la cancion
 * @param ext lista de formatos de descarga
 */
data class SongIdModel(
    val id: Int = 0,
    val name: String = "",
    val ext: String = ""
)

data class ResponseOnichan(
    val url: String,
    val name: String,
    val ext: String,
    val id: Int
)

data class Song(
    val id: Int = 0,
    val name: String = "",
    val size: String = "",
    val image: String = "",
    val ext: String = ""
)

/**
 * Maneja la respuesta al eliminar una cancion de la lista de descargas
 * @param isDelete indica si se elimino la cancion de la lista de descargas
 */
data class SongDeleteModel(
    val isDelete: Boolean = false
 )
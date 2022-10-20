package com.m_and_a_company.canelatube.domain.network

import com.m_and_a_company.canelatube.domain.data.models.DIRECTORY_NAME_SAVE_SONGS
import java.io.File

const val BASE_URL = "http://192.168.1.66:3333/api/v1/"
const val DEBUG_URL = "https://canelatube.alwaysdata.net/api/v1/"
const val GET_INFO_PREFIX = "info"
const val GET_SONG_INFO = "$GET_INFO_PREFIX/song"
const val GET_VIDEO_INFO = "$GET_INFO_PREFIX/video"
const val POST_PREPARE_PREFIX = "prepare"
const val DOWNLOAD_SONG_ENDPOINT = "download"
const val DELETE_ENDPOINT = "delete"
const val GET_INFO_ENDPOINT_KEY_QUERY_PARAM = "url"
const val PREPARE_SONG_KEY_URL_PARAM = "url"
const val PREPARE_SONG_KEY_ITAG_PARAM = "itag"
const val DOWNLOAD_SONG_QUERY_PARAM = "id"
const val WAIT_TIMEOUT = 60L
const val CONNECT_TIMEOUT = 60L
const val DEFAULT_NAME_FILE = "undefined_name.mp3"

val PATH_DOWNLOAD = File(DIRECTORY_NAME_SAVE_SONGS)
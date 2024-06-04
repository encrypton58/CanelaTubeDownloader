package com.m_and_a_company.canelatube.domain.network

import com.m_and_a_company.canelatube.BuildConfig

const val BASE_URL = BuildConfig.base_url
const val PREFIX_GET_FORMATS_URL = "onichan"
const val PREFIX_DELETE_SONG_ONICHAN = "onichan/{id}"
const val PREFIX_GET_SONGS_URL = "music/songs"
const val URL_PARAM = "url"
const val ITAG_PARAM = "itag"
const val ID_URL_PARAM = "id"
const val WAIT_TIMEOUT = 20L
const val CONNECT_TIMEOUT = 20L

//val PATH_DOWNLOAD = File(DIRECTORY_NAME_SAVE_SONGS)
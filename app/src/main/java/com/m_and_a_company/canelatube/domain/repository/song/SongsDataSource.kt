package com.m_and_a_company.canelatube.domain.repository.song

import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.model.ResponseService
import com.m_and_a_company.canelatube.network.domain.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.network.domain.model.ResponseApi
import com.m_and_a_company.canelatube.network.domain.model.SongIdModel
import com.m_and_a_company.canelatube.ui.enums.TypeDownload

interface SongsDataSource {

    suspend fun getInfoSongFromUrl(url: String): ResponseApi.Success<MusicDownloadsModel>

    suspend fun getIdSong(url: String, iTag: Int): ResponseApi.Success<SongIdModel>

    suspend fun getInfoVideoFromUtl(url: String): ResponseService

    suspend fun prepareSongToDownload(typeDownload: TypeDownload, url: String, iTag: Int): ResponseService

    suspend fun downloadSong(id: Int)

    suspend fun finishDownloadSong(songId: Int, listener: DownloadFinishedListener)

}
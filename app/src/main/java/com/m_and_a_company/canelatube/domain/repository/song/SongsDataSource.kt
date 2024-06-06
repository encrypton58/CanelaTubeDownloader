package com.m_and_a_company.canelatube.domain.repository.song

import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel

interface SongsDataSource {

    suspend fun getSongs(): ResponseApi.Success<List<Song>>

    suspend fun getInfoSongFromUrl(url: String): DaisonResponseService<ResultSet<MusicDownloadsModel>>

    suspend fun getIdSong(url: String, iTag: Int): ResponseApi.Success<SongIdModel>

    suspend fun downloadSong(song: DownloadSong)

    suspend fun finishDownloadSong(songId: Int, listener: DownloadFinishedListener?): ResponseApi.Success<Boolean>

}
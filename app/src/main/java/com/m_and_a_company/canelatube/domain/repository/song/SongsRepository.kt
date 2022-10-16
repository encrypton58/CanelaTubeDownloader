package com.m_and_a_company.canelatube.domain.repository.song

import android.content.Context
import com.m_and_a_company.canelatube.domain.network.client.MusicApiService
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.network.domain.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.network.domain.model.ResponseApi
import com.m_and_a_company.canelatube.network.domain.model.SongIdModel

class SongsRepository(private val context: Context): SongsDataSource {

    private val musicApiService = MusicApiService(context)

    override suspend fun getInfoSongFromUrl(url: String): ResponseApi.Success<MusicDownloadsModel> {
        return musicApiService.getListDownload(url)
    }

    override suspend fun getIdSong(
        url: String,
        iTag: Int
    ): ResponseApi.Success<SongIdModel> {
        return musicApiService.getIdSong(url, iTag)
    }

    override suspend fun downloadSong(id: Int) {
        musicApiService.downloadSong(id, context)
    }

    override suspend fun finishDownloadSong(songId: Int, listener: DownloadFinishedListener) {
        musicApiService.setListener(listener)
        musicApiService.finishDownload(songId)
    }

}
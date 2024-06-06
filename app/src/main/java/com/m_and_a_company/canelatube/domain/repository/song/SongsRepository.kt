package com.m_and_a_company.canelatube.domain.repository.song

import android.content.Context
import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.client.MusicApiService
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel

class SongsRepository(private val context: Context): SongsDataSource {

    private val musicApiService = MusicApiService(context)

    override suspend fun getSongs(): ResponseApi.Success<List<Song>> {
        return musicApiService.getSongs()
    }

    override suspend fun getInfoSongFromUrl(url: String): DaisonResponseService<ResultSet<MusicDownloadsModel>> {
        return musicApiService.getListDownload(url)
    }

    override suspend fun getIdSong(
        url: String,
        iTag: Int
    ): ResponseApi.Success<SongIdModel> {
        return musicApiService.getIdSong(url, iTag)
    }

    override suspend fun downloadSong(song: DownloadSong) {
        musicApiService.downloadSong(song, context)
    }

    override suspend fun finishDownloadSong(songId: Int, listener: DownloadFinishedListener?): ResponseApi.Success<Boolean> {
        if(listener != null) {
            musicApiService.setListener(listener)
        }

        return musicApiService.finishDownload(songId)
    }

}
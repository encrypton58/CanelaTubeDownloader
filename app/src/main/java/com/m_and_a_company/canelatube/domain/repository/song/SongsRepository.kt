package com.m_and_a_company.canelatube.domain.repository.song

import android.content.Context
import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.client.MusicApiService
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi
import com.m_and_a_company.canelatube.domain.network.model.ResponseOnichan
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel

class SongsRepository(private val context: Context) : SongsDataSource {

    private val musicApiService = MusicApiService(context)

    override suspend fun getInfoSongFromUrl(url: String): DaisonResponseService<ResultSet<MusicDownloadsModel>> =
        musicApiService.getListDownload(url)

    override suspend fun getIdSong(
        url: String,
        iTag: Int
    ): DaisonResponseService<ResultSet<ResponseOnichan>> =
        musicApiService.getDownloadSong(url, iTag)

    override suspend fun finishDownloadSong(
        songId: Int,
        listener: DownloadFinishedListener?
    ): ResponseApi.Success<Boolean> {
        if (listener != null) {
            musicApiService.setListener(listener)
        }

        return musicApiService.finishDownload(songId)
    }

    override suspend fun downloadFromOnichan(name: String, id: Int, ext: String, url: String) {
        musicApiService.downloadSongFromOnichan(name, id, ext, url, context)
    }

}
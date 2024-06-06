package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class GetInfoSongFromUrlUseCase(private val songsRepository: SongsDataSource) {

    suspend fun execute(url: String): DaisonResponseService<ResultSet<MusicDownloadsModel>> {
        return songsRepository.getInfoSongFromUrl(url)
    }

}
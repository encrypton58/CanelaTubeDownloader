package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi

class GetInfoSongFromUrlUseCase(private val songsRepository: SongsDataSource) {

    suspend fun execute(url: String) = songsRepository.getInfoSongFromUrl(url)
    

}
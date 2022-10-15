package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.network.model.ResponseService
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource
import com.m_and_a_company.canelatube.ui.enums.TypeDownload

class PrepareSongToDownloadUseCase(private val songsRepository: SongsDataSource) {


    suspend operator fun invoke(typeDownload: TypeDownload, url: String, iTag: Int): ResponseService{
        return songsRepository.prepareSongToDownload(typeDownload,url,iTag)
    }

}
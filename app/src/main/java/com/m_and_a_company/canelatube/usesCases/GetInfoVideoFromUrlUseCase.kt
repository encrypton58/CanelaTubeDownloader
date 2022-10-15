package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.network.model.ResponseService
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class GetInfoVideoFromUrlUseCase(private val repository: SongsDataSource) {

    suspend operator fun invoke(url: String): ResponseService{
        return repository.getInfoVideoFromUtl(url)
    }

}
package com.m_and_a_company.canelatube.usesCases


import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class GetSongsUseCase(private val songsRepository: SongsDataSource) {

    suspend operator fun invoke() = songsRepository.getSongs()

}
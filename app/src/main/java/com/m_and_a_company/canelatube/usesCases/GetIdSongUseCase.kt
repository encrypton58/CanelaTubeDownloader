package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class GetIdSongUseCase(private val repository: SongsDataSource) {

    suspend operator fun invoke(url: String, iTag: Int) = repository.getIdSong(url, iTag)

}
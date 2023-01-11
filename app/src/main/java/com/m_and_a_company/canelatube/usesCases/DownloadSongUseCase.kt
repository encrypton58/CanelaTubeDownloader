package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class DownloadSongUseCase(private val songsRepository: SongsDataSource) {

    suspend operator fun invoke(song: DownloadSong) {
        songsRepository.downloadSong(song)
    }

}
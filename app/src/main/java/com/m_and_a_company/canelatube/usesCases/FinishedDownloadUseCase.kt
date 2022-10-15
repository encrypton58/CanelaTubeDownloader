package com.m_and_a_company.canelatube.usesCases

import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.repository.song.SongsDataSource

class FinishedDownloadUseCase(private val songsRepository: SongsDataSource) {

    suspend fun execute(idSong: Int, downloadFinished: DownloadFinishedListener) {
        songsRepository.finishDownloadSong(idSong, downloadFinished)

    }

}
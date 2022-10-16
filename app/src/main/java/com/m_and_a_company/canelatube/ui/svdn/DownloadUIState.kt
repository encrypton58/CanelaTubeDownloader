package com.m_and_a_company.canelatube.ui.svdn

import com.m_and_a_company.canelatube.network.domain.model.ErrorModel
import com.m_and_a_company.canelatube.network.domain.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.network.domain.model.SongIdModel

sealed class DownloadUIState {

    object Loading : DownloadUIState()

    data class Success(val musicDownloadsModel: MusicDownloadsModel) : DownloadUIState()

    data class SuccessGetSongId(val song: SongIdModel) : DownloadUIState()

    data class Error(val message: String, val errors: List<ErrorModel>) : DownloadUIState()

}

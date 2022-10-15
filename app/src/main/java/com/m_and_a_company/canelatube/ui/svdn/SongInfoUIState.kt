package com.m_and_a_company.canelatube.ui.svdn

import com.m_and_a_company.canelatube.domain.data.models.SongInfo
import com.m_and_a_company.canelatube.domain.data.models.VideoInfo
import com.m_and_a_company.canelatube.domain.network.model.RequestErrors

sealed class SongInfoUIState{

    object Loading : SongInfoUIState()

    data class SuccessToGetInfo(val songInfo: SongInfo) : SongInfoUIState()

    data class SuccessToGetInfoVideo(val videoInfo: VideoInfo) : SongInfoUIState()

    object SuccessPrepareToDownload : SongInfoUIState()

    data class Error(val error: RequestErrors) : SongInfoUIState()

}

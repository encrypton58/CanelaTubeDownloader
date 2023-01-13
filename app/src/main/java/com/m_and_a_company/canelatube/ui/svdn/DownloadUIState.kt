package com.m_and_a_company.canelatube.ui.svdn

import com.m_and_a_company.canelatube.domain.network.enum.TypeError
import com.m_and_a_company.canelatube.domain.network.model.ErrorModel
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel

sealed class DownloadUIState {

    object Loading : DownloadUIState()

    data class Success(val musicDownloadsModel: MusicDownloadsModel) : DownloadUIState()

    data class SuccessGetSongId(val song: SongIdModel) : DownloadUIState()

    data class SuccessSongs(val songs: List<Song>) : DownloadUIState()

    data class SuccessDelete(val isDelete: Boolean, val positionRemove: Int): DownloadUIState()

    data class Error(val message: String, val errors: List<ErrorModel>?, val type: TypeError? = TypeError.UNDEFINED) : DownloadUIState()

    object ClearState : DownloadUIState()

    fun Error.getMessageFromErrors(): String? {
        if(errors != null && errors.isNotEmpty()) {
            var errorText = ""
            val requiredSpaceBottom = errors.size > 1
            errors.forEach { errorModel ->
                errorText += "Campo: ${errorModel.field} \n"
                errorText += "regla del campo: ${errorModel.rule} \n\n"
                errorText += "${errorModel.message} \n"
                if (requiredSpaceBottom) {
                    errorText += "\n"
                }

            }
            return errorText
        }
        return null
    }

}

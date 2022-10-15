package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.network.domain.enum.ResponseStatus
import com.m_and_a_company.canelatube.usesCases.DownloadSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetIdSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetInfoSongFromUrlUseCase
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val getInfoSongFromUrlUseCase: GetInfoSongFromUrlUseCase,
    private val getIdSongUseCase: GetIdSongUseCase,
    private val downloadSongUseCase: DownloadSongUseCase
) : ViewModel() {

    private val mutableLiveDataViewState: MutableLiveData<DownloadUIState> = MutableLiveData()
    val viewState: LiveData<DownloadUIState>
        get() = mutableLiveDataViewState

    fun getInfoSongFromUrl(url: String) {
        viewModelScope.launch {
            val result = getInfoSongFromUrlUseCase.execute(url)
            when (ResponseStatus.fromInt(result.statusCode)) {
                ResponseStatus.SUCCESS -> {
                    mutableLiveDataViewState.value = DownloadUIState.Success(result.data!!)
                }
                ResponseStatus.ERROR_CODE -> TODO()
                ResponseStatus.ERROR_CLIENT -> TODO()
                ResponseStatus.ERROR_DB -> TODO()
            }
        }
    }

    fun getIdSong(url: String, iTag: Int) {
        viewModelScope.launch {
            val result = getIdSongUseCase(url, iTag)
            when (ResponseStatus.fromInt(result.statusCode)) {
                ResponseStatus.SUCCESS -> {
                    mutableLiveDataViewState.value = DownloadUIState.SuccessGetSongId(result.data!!)
                }
                ResponseStatus.ERROR_CODE -> TODO()
                ResponseStatus.ERROR_CLIENT -> TODO()
                ResponseStatus.ERROR_DB -> TODO()
            }
        }
    }

    fun downloadSong(id: Int) {
        viewModelScope.launch {
            downloadSongUseCase(id)
        }
    }

}
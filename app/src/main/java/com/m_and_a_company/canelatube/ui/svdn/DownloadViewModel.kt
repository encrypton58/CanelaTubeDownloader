package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.enum.ResponseStatus
import com.m_and_a_company.canelatube.domain.network.exceptions.SongException
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
            try {
                val result = getInfoSongFromUrlUseCase.execute(url)
                when (ResponseStatus.fromInt(result.statusCode)) {
                    ResponseStatus.SUCCESS -> {
                        mutableLiveDataViewState.value = DownloadUIState.Success(result.data!!, result.warningMessage)
                    }
                    else -> {
                        mutableLiveDataViewState.value = DownloadUIState.Error("Codigo de estado: ${result.statusCode}", emptyList())
                    }
                }
            } catch (e: SongException) {
                mutableLiveDataViewState.value = DownloadUIState.Error(e.message!!, e.getErrors())
            }
        }
    }

    fun getIdSong(url: String, iTag: Int) {
        mutableLiveDataViewState.postValue(DownloadUIState.Loading)
        viewModelScope.launch {
            try{
                val result = getIdSongUseCase(url, iTag)
                when (ResponseStatus.fromInt(result.statusCode)) {
                    ResponseStatus.SUCCESS -> {
                        mutableLiveDataViewState.value = DownloadUIState.SuccessGetSongId(result.data!!)
                    }
                    else -> {
                        mutableLiveDataViewState.value = DownloadUIState.Error("Codigo de estado: ${result.statusCode}", emptyList())
                    }
                }
            } catch (e: SongException) {
                mutableLiveDataViewState.value = DownloadUIState.Error(e.message!!, e.getErrors())
            }
        }
    }

    fun downloadSong(id: Int) {
        viewModelScope.launch {
            downloadSongUseCase(DownloadSong(id, true, true))
        }
    }

}
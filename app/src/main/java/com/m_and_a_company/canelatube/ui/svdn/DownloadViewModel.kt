package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.enum.ResponseStatus
import com.m_and_a_company.canelatube.domain.network.exceptions.SongException
import com.m_and_a_company.canelatube.domain.network.model.ResponseOnichan
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

            when (val result = getInfoSongFromUrlUseCase.execute(url)) {
                is DaisonResponseService.Error -> {
                    mutableLiveDataViewState.value =
                        DownloadUIState.Error(result.message, emptyList())
                    return@launch
                }

                is DaisonResponseService.Success -> {
                    val resultSet = result.data.resultSet
                    mutableLiveDataViewState.value =
                        DownloadUIState.Success(resultSet, resultSet.warningMessage)
                    return@launch
                }
            }
        }
    }

    fun getIdSong(url: String, iTag: Int) {
        mutableLiveDataViewState.postValue(DownloadUIState.Loading)
        viewModelScope.launch {
            when (val result = getIdSongUseCase(url, iTag)) {
                is DaisonResponseService.Error ->
                    mutableLiveDataViewState.value =
                        DownloadUIState.Error(result.message, emptyList())

                is DaisonResponseService.Success -> {
                    mutableLiveDataViewState.value =
                        DownloadUIState.SuccessOnichan(result.data.resultSet)
                }
            }
        }
    }

    fun downloadSong(response: ResponseOnichan) {
        viewModelScope.launch {
            downloadSongUseCase(response)
        }
    }

}
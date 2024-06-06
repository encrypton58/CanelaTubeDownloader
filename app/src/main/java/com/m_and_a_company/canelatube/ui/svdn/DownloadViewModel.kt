package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.core.daison.DaisonResponseService
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.enum.ResponseStatus
import com.m_and_a_company.canelatube.domain.network.exceptions.SongException
import com.m_and_a_company.canelatube.usesCases.DownloadSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetIdSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetInfoSongFromUrlUseCase
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getInfoSongFromUrlUseCase.execute(url)) {
                is DaisonResponseService.Error -> mutableLiveDataViewState.postValue(
                    DownloadUIState.Error("Codigo de estado: ${result.statusCode}", emptyList())
                )

                is DaisonResponseService.Success -> mutableLiveDataViewState.postValue(
                    DownloadUIState.Success(result.data.data, result.data.warningMessage)
                )
            }
        }
    }

    fun getIdSong(url: String, iTag: Int) {
        mutableLiveDataViewState.postValue(DownloadUIState.Loading)
        viewModelScope.launch {
            try {
                val result = getIdSongUseCase(url, iTag)
                when (ResponseStatus.fromInt(result.statusCode)) {
                    ResponseStatus.SUCCESS -> {
                        mutableLiveDataViewState.value =
                            DownloadUIState.SuccessGetSongId(result.data!!)
                    }

                    else -> {
                        mutableLiveDataViewState.value = DownloadUIState.Error(
                            "Codigo de estado: ${result.statusCode}",
                            emptyList()
                        )
                    }
                }
            } catch (e: SongException) {
                mutableLiveDataViewState.value = DownloadUIState.Error(e.message!!, e.getErrors())
            }
        }
    }

    fun downloadSong(id: Int) {
        viewModelScope.launch {
            downloadSongUseCase(DownloadSong(id, requiredDelete = true, requiredGetName = true))
        }
    }

}
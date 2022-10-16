package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SVDNViewModel(
): ViewModel(), OnSelectTypeDownload {

    private val mutableSong = MutableLiveData<SongInfoUIState>()
    val songViewState: LiveData<SongInfoUIState> get() = mutableSong
    private val typeDownload = MutableLiveData<TypeDownload>()
    val typeDownloadViewState: LiveData<TypeDownload> get() = typeDownload
    val selectToDownloadListener = this

    fun getInfoSongFromUrl() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun prepareSongToDownload() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun getInfoVideoFromUrl() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun downloadSong() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    override fun onAccept(type: TypeDownload) {
        this.typeDownload.value = type
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

}
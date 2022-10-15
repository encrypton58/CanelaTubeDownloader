package com.m_and_a_company.canelatube.ui.svdn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m_and_a_company.canelatube.domain.data.models.REQUEST_ERRORS_TYPE
import com.m_and_a_company.canelatube.domain.data.models.SONG_INFO_TYPE
import com.m_and_a_company.canelatube.domain.data.models.SongInfo
import com.m_and_a_company.canelatube.domain.data.models.VIDEO_INFO_TYPE
import com.m_and_a_company.canelatube.domain.data.models.VideoInfo
import com.m_and_a_company.canelatube.domain.network.BAD_REQUEST_CODE
import com.m_and_a_company.canelatube.domain.network.EXCEPTION_CODE_ERROR
import com.m_and_a_company.canelatube.domain.network.NOT_FOUND_CODE
import com.m_and_a_company.canelatube.domain.network.SUCCESS_CODE_OK
import com.m_and_a_company.canelatube.domain.network.UNPROCESSED_ENTITY_CODE
import com.m_and_a_company.canelatube.domain.network.model.ResponseFromConvert
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload
import com.m_and_a_company.canelatube.usesCases.DownloadSongUseCase
import com.m_and_a_company.canelatube.usesCases.GetInfoSongFromUrlUseCase
import com.m_and_a_company.canelatube.usesCases.GetInfoVideoFromUrlUseCase
import com.m_and_a_company.canelatube.usesCases.PrepareSongToDownloadUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SVDNViewModel(
    private val getInfoSongFromUrlUseCase: GetInfoSongFromUrlUseCase,
    private val getInfoVideoFromUrlUseCase: GetInfoVideoFromUrlUseCase,
    private val prepareSongToDownloadUseCase: PrepareSongToDownloadUseCase,
    private val downloadSongUseCase: DownloadSongUseCase,
): ViewModel(), OnSelectTypeDownload {

    private val mutableSong = MutableLiveData<SongInfoUIState>()
    val songViewState: LiveData<SongInfoUIState> get() = mutableSong
    private val typeDownload = MutableLiveData<TypeDownload>()
    val typeDownloadViewState: LiveData<TypeDownload> get() = typeDownload
    val selectToDownloadListener = this

    private lateinit var songUrl: String

    fun getInfoSongFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultSet = getInfoSongFromUrlUseCase.execute(url)

        }
    }

    fun prepareSongToDownload(iTag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultSet = prepareSongToDownloadUseCase(typeDownload.value!!, songUrl, iTag)
            when(resultSet.code){
                SUCCESS_CODE_OK -> {
                    val responseFromConvert = resultSet.data as ResponseFromConvert
                    SVDN.idSongDownload = responseFromConvert.idDownload
                    mutableSong.postValue(SongInfoUIState.SuccessPrepareToDownload)
                }

                UNPROCESSED_ENTITY_CODE ->{
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }

                BAD_REQUEST_CODE -> {
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }

                NOT_FOUND_CODE -> {
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }

                EXCEPTION_CODE_ERROR -> {
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }

            }
        }
    }

    fun getInfoVideoFromUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultSet = getInfoVideoFromUrlUseCase(url)
            when(resultSet.code){
                SUCCESS_CODE_OK ->{
                    if(resultSet.toParse == VIDEO_INFO_TYPE){
                        val videoInfo = resultSet.data as VideoInfo
                        songUrl = url
                        mutableSong.postValue(SongInfoUIState.SuccessToGetInfoVideo(videoInfo))
                    }
                }
                UNPROCESSED_ENTITY_CODE ->{
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }
                BAD_REQUEST_CODE -> {
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }
                EXCEPTION_CODE_ERROR -> {
                    if(resultSet.toParse == REQUEST_ERRORS_TYPE){
                        mutableSong.postValue(SongInfoUIState.Error(resultSet.errors))
                    }
                }
            }
        }
    }

    fun downloadSong() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun setTypeDownload(typeDownload: TypeDownload){
        this.typeDownload.postValue(typeDownload)
    }

    override fun onAccept(type: TypeDownload) {
        this.typeDownload.value = type
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

}
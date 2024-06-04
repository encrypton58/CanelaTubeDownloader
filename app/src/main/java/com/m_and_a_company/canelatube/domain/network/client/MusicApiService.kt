package com.m_and_a_company.canelatube.domain.network.client

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.m_and_a_company.canelatube.core.daison.DaisonApiCallImp
import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.data.models.DownloadSong
import com.m_and_a_company.canelatube.domain.network.BASE_URL
import com.m_and_a_company.canelatube.domain.network.CONNECT_TIMEOUT
import com.m_and_a_company.canelatube.domain.network.NetworkModule
import com.m_and_a_company.canelatube.domain.network.NetworkPreferences
import com.m_and_a_company.canelatube.domain.network.WAIT_TIMEOUT
import com.m_and_a_company.canelatube.domain.network.enum.ResponseStatus
import com.m_and_a_company.canelatube.domain.network.enum.TypeError
import com.m_and_a_company.canelatube.domain.network.exceptions.SongException
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.interfaces.MusicService
import com.m_and_a_company.canelatube.domain.network.model.ErrorModel
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel
import com.m_and_a_company.canelatube.environment.getPathSongs
import com.m_and_a_company.canelatube.environment.service.DownloadedListenerService
import com.m_and_a_company.canelatube.getDownloadManager
import com.m_and_a_company.canelatube.getRequestDownload
import com.m_and_a_company.canelatube.getRequestOnichan
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class MusicApiService(private val context: Context) : DaisonApiCallImp() {

    companion object {
        const val TAG = "MusicApiService"
    }

    private fun buildHttpClient(): Retrofit {

        val okHttpClient = OkHttpClient.Builder().apply {
            readTimeout(WAIT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            connectTimeout(CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

    }

    private lateinit var failResponse: ResponseApi.Error

    private val mService = buildHttpClient().create(MusicService::class.java)

    private var listener: DownloadFinishedListener? = null

    fun setListener(listener: DownloadFinishedListener) {
        this.listener = listener
    }

    suspend fun getSongs(): ResponseApi.Success<List<Song>> {
        return this.executeService {

            val result = mService.getSongs()

            if (!result.isSuccessful) {
                Log.e(TAG, result.errorBody().toString())
                failResponse = NetworkModule.parseErrorResponse(
                    result.errorBody(),
                    ResponseApi.Error::class.java
                ) ?: ResponseApi.Error(2, "Ocurrio un error")
                failResponse.errors?.let {
                    throw SongException(failResponse.message!!, it, failResponse.statusCode)
                } ?: throw SongException(failResponse.message!!, null, failResponse.statusCode)
            }
            val preferences = NetworkModule.provideNetworkPreferences(context)

            result.body()?.data?.forEach {
                preferences.saveJsonDataGetSong(it)
            }

            ResponseApi.Success(
                result.body()!!.statusCode,
                result.body()!!.data,
                result.body()!!.message
            )

        }

    }

    suspend fun getListDownload(url: String) = safeApiCall {
        mService.getMusic(url)
    }

    suspend fun getDownloadSong(url: String, formatId: Int) = safeApiCall {
        mService.getDownloadSong(url, formatId)
    }


    suspend fun getIdSong(url: String, iTag: Int): ResponseApi.Success<SongIdModel> {
        return this.executeService {
            val result = mService.getSongId(url, iTag)
            if (!result.isSuccessful) {
                Log.e(TAG, result.errorBody().toString())
                failResponse = NetworkModule.parseErrorResponse(
                    result.errorBody(),
                    ResponseApi.Error::class.java
                ) ?: ResponseApi.Error(2, "Ocurrio un error")
                failResponse.errors?.let {
                    throw SongException(failResponse.message!!, it, failResponse.statusCode)
                }
                throw SongException(failResponse.message!!, null, failResponse.statusCode)
            }
            NetworkModule.provideNetworkPreferences(context).apply {
                saveNameFile(result.body()!!.data!!.name)
                saveExtensionFile(result.body()!!.data!!.ext)
            }
            ResponseApi.Success(
                result.body()!!.statusCode,
                result.body()!!.data,
                result.body()!!.message
            )
        }

    }

    fun downloadSong(downloadSong: DownloadSong, context: Context) {
        val downloadManager = getDownloadManager(context)
        val request = getRequestDownload(downloadSong.id)
        val preferences = NetworkModule.provideNetworkPreferences(context)
        val idDownload = downloadUnification(downloadSong, preferences, request, downloadManager)
        preferences.setIdFromDownloadManager(idDownload)
        preferences.setRequireCurrentDownloadDelete(downloadSong.requiredDelete)
        val serviceIntent = Intent(context, DownloadedListenerService::class.java)
        context.startService(serviceIntent)
    }

    fun downloadSongFromOnichan(name: String, id: Int, ext: String, url: String, context: Context) {
        val downloadManager = getDownloadManager(context)
        val request = getRequestOnichan(url)
        val preferences = NetworkModule.provideNetworkPreferences(context)
        val idDownload = downloadOnichan(name, id, ext, downloadManager, request, preferences)
        preferences.setIdFromDownloadManager(idDownload)
        preferences.setRequireCurrentDownloadDelete(true)
        val serviceIntent = Intent(context, DownloadedListenerService::class.java)
        context.startService(serviceIntent)
    }

    private fun downloadOnichan(name: String, id: Int, ext: String, downloadManager: DownloadManager, request: Request, networkPreferences: NetworkPreferences): Long {
        val nameFile = "$name.$ext"
        networkPreferences.setIdToDownload(id)
        request.setTitle(nameFile)
        request.setMimeType("audio/mpeg")
        request.setDescription("Descargando")
        request.setDestinationUri(Uri.fromFile(getPathSongs(nameFile)))
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE)
        return downloadManager.enqueue(request)
    }

    private fun downloadUnification(
        downloadSong: DownloadSong,
        preferences: NetworkPreferences,
        request: Request,
        downloadManager: DownloadManager
    ): Long {
        val extFileSave: String
        val nameFile: String
        val nameFileSave: String
        if (downloadSong.requiredDelete) {
            preferences.setIdToDownload(downloadSong.id)
            extFileSave = preferences.getExtensionFile()
            nameFile = preferences.getNameFile()
            nameFileSave = "$nameFile$extFileSave"
        } else {
            val song = preferences.getJsonDataGetSong(downloadSong.id)
            extFileSave = song.ext
            nameFile = song.name
            nameFileSave = "$nameFile$extFileSave"
        }
        val titleNotification = StringBuilder("Descargando ").append(nameFile)
        request.setTitle(titleNotification)
        request.setMimeType("audio/mpeg")
        request.setDescription("Descargando")
        request.setDestinationUri(Uri.fromFile(getPathSongs(nameFileSave)))
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE)
        return downloadManager.enqueue(request)
    }

    suspend fun finishDownload(id: Int): ResponseApi.Success<Boolean> {
        return this.executeService {
            val result = mService.deleteSong(id)
            if (!result.isSuccessful) {
                throw SongException(result.message())
            }
            val isDeleteSong = result.body()!!.resultSet.isDelete
            listener?.onFinish(isDeleteSong)

            ResponseApi.Success(
                result.body()!!.statusCode,
                isDeleteSong,
                result.body()!!.message
            )
        }
    }

    @Throws
    private suspend fun <T> executeService(lambda: suspend () -> T): T {
        try {
            return lambda()
        } catch (e: ConnectException) {
            throw SongException(
                "No se pudo conectar con el servidor",
                null,
                ResponseStatus.ERROR_CLIENT.ordinal
            )
        } catch (e: SocketTimeoutException) {
            throw SongException(
                "Se agoto el tiempo de espera",
                null,
                ResponseStatus.ERROR_CLIENT.ordinal
            )
        } catch (e: java.net.UnknownHostException) {
            throw SongException(
                "Ocurrio un error al conectar con el servicio",
                arrayListOf(
                    ErrorModel("enabled", "internet", "Necesita estar conectado a la red"),
                    ErrorModel(
                        "enabled",
                        "service",
                        "Puede que el servicio este en mantenimiento pruebe mas tarde"
                    )
                ), ResponseStatus.ERROR_CLIENT.ordinal,
                TypeError.INTERNET_OR_SERVER
            )
        }
    }


}
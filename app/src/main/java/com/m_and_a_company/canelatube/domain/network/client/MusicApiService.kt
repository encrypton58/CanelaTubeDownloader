package com.m_and_a_company.canelatube.domain.network.client

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import com.m_and_a_company.canelatube.domain.network.CONNECT_TIMEOUT
import com.m_and_a_company.canelatube.domain.network.DEBUG_URL
import com.m_and_a_company.canelatube.domain.network.NetworkModule
import com.m_and_a_company.canelatube.domain.network.WAIT_TIMEOUT
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.enviroment.DownloadBroadcast
import com.m_and_a_company.canelatube.enviroment.getPathSongs
import com.m_and_a_company.canelatube.network.domain.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.network.domain.model.ResponseApi
import com.m_and_a_company.canelatube.network.domain.model.SongIdModel
import com.m_and_a_company.canelatube.network.interfaces.MusicService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder

class MusicApiService(private val context: Context) {

    private fun buildHttpClient(): Retrofit {

        val okHttpClient = OkHttpClient.Builder().apply {
            readTimeout(WAIT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            connectTimeout(CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
        }

        return Retrofit.Builder()
            .baseUrl(DEBUG_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

    }

    private val mService = buildHttpClient().create(MusicService::class.java)

    private var listener: DownloadFinishedListener? = null

    fun setListener(listener: DownloadFinishedListener) {
        this.listener = listener
    }

    suspend fun getListDownload(url: String): ResponseApi.Success<MusicDownloadsModel> {
        val result = mService.getMusic(url)
        if(!result.isSuccessful) {
            throw Exception(result.message())
        }
        return ResponseApi.Success(
            result.body()!!.statusCode,
            result.body()!!.data,
            result.body()!!.message
        )
    }

    suspend fun getIdSong(url: String, iTag: Int): ResponseApi.Success<SongIdModel> {
        val result = mService.getSongId(url, iTag)
        if(!result.isSuccessful) {
            throw Exception(result.message())
        }
        NetworkModule.provideNetworkPreferences(context).apply {
            saveNameFile(result.body()!!.data!!.name)
            saveExtensionFile(result.body()!!.data!!.ext)
        }
        return ResponseApi.Success(
            result.body()!!.statusCode,
            result.body()!!.data,
            result.body()!!.message
        )
    }

    fun downloadSong(id: Int, context: Context) {
        context.registerReceiver(
            DownloadBroadcast(),
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse("${DEBUG_URL}music/download/$id"))
        NetworkModule.provideNetworkPreferences(context).setIdToDownload(id)
        val extFileSave = NetworkModule.provideNetworkPreferences(context).getExtensionFile()
        val nameFile = NetworkModule.provideNetworkPreferences(context).getNameFile()
        val titleNotification = StringBuilder("Descargando ").append(NetworkModule.provideNetworkPreferences(context).getNameFile()).toString()
        request.setTitle(titleNotification)
        request.setMimeType("audio/mp3")
        request.setDescription("Descargando")
        val nameFileSave = "$nameFile$extFileSave"
        request.setDestinationUri(Uri.fromFile(getPathSongs(nameFileSave)))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
    }

    suspend fun finishDownload(id: Int) {

        val result = mService.deleteSong(id)
        if(!result.isSuccessful) {
            throw Exception(result.message())
        }

        listener?.onFinish(result.body()!!.data!!.isDelete)

    }

}
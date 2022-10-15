package com.m_and_a_company.canelatube.domain.network

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import com.google.gson.Gson
import com.m_and_a_company.canelatube.domain.network.api.MusicDownloadService
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadSongListener
import com.m_and_a_company.canelatube.domain.network.model.RequestErrors
import com.m_and_a_company.canelatube.domain.network.model.RequestParamError
import com.m_and_a_company.canelatube.domain.network.model.ResponseService
import com.m_and_a_company.canelatube.enviroment.DownloadBroadcast
import com.m_and_a_company.canelatube.enviroment.UtilsEnvironment
import com.m_and_a_company.canelatube.enviroment.getPathSongs
import com.m_and_a_company.canelatube.enviroment.service.DownloadStatus
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder.decode
import java.nio.charset.StandardCharsets

class NetworkManager(private val context: Context) {

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

    private val mService = buildHttpClient().create(MusicDownloadService::class.java)

    suspend fun getInfoSongFromUrl(url: String): ResponseService {
        try {
            val responseResult = mService.infoSongFromUrl(url)
            if (responseResult.isSuccessful) {
                return ResponseService(
                    responseResult.code(),
                    responseResult.body()!!,
                    responseResult.body().toString()
                )
            } else {
                val responseErrors = Gson().fromJson(
                    responseResult.errorBody()?.string(),
                    RequestErrors::class.java
                )
                return ResponseService(
                    responseResult.code(),
                    responseResult.message(),
                    RequestErrors(listOf(), 0, "").toString(),
                    responseErrors
                )
            }
        } catch (e: Exception) {
            val errors = RequestErrors(
                listOf(RequestParamError("Internal", "", e.message.toString()))
            )
            e.printStackTrace()
            return ResponseService(
                EXCEPTION_CODE_ERROR,
                "",
                RequestErrors(listOf(), 0, "").toString(),
                errors
            )
        }
    }

    suspend fun getInfoVideoFromUrl(url: String): ResponseService {
        try {
            val resultSet = mService.infoVideoFromUrl(url)
            if(resultSet.isSuccessful){
                return ResponseService(
                    resultSet.code(),
                    resultSet.body()!!,
                    resultSet.body().toString()
                )
            }else {
                val responseErrors = Gson().fromJson(
                    resultSet.errorBody()?.string(),
                    RequestErrors::class.java
                )
                return ResponseService(
                    resultSet.code(),
                    resultSet.message(),
                    RequestErrors(listOf(), 0, "").toString(),
                    responseErrors
                )
            }
        }catch (e: Exception) {
            val errors = RequestErrors(
                listOf(RequestParamError("Internal", "", e.message.toString()))
            )
            e.printStackTrace()
            return ResponseService(
                EXCEPTION_CODE_ERROR,
                "",
                RequestErrors(listOf(), 0, "").toString(),
                errors
            )
        }

    }

    suspend fun prepareSongToDownload(typeDownload: TypeDownload, url: String, iTag: Int): ResponseService {
        try {
            val responseResult =
                mService.prepareSong(typeDownload.getType(), url, iTag.toString())
            if (responseResult.isSuccessful) {
                NetworkModule.provideNetworkPreferences(context).apply {
                    saveNameFile(responseResult.body()!!.nameFile)
                    saveExtensionFile(responseResult.body()!!.extensionFile)
                }
                return ResponseService(
                    responseResult.code(),
                    responseResult.body()!!,
                    responseResult.body().toString()
                )
            } else {
                val responseErrors = Gson().fromJson(
                    responseResult.errorBody()?.string(),
                    RequestErrors::class.java
                )
                return ResponseService(
                    responseResult.code(),
                    responseResult.message(),
                    RequestErrors(listOf(), 0, "").toString(),
                    responseErrors
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseService(
                EXCEPTION_CODE_ERROR,
                e.message ?: "Ocurrio Un Error",
                e.toString()
            )
        }
    }

    suspend fun downloadSong(
        songId: Int,
        downloadSongListener: DownloadSongListener
    ): String {
        try {
            val responseResult = mService.downloadSong(songId)
            if (responseResult.isSuccessful) {
                val filename =
                    getNamesFromHeaders(responseResult.headers().names(), responseResult.headers())
                responseResult.body()?.apply {
                    downloadWithProgress(filename).collect {
                        when (it) {
                            is DownloadStatus.Downloading -> {
                                downloadSongListener.onProgress(it.progress)
                            }
                            is DownloadStatus.Downloaded -> {
                                downloadSongListener.onFinished(it.isSave)
                            }
                        }
                    }
                }
                return ""
            } else {
                val responseErrors = Gson().fromJson(
                    responseResult.errorBody()?.string(),
                    RequestErrors::class.java
                )
                return Utils.buildMessageError(responseErrors)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return e.message.toString()
        }
    }

    fun download(songId: Int) {
        context.registerReceiver(
            DownloadBroadcast(),
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =
            DownloadManager.Request(Uri.parse("$BASE_URL$DOWNLOAD_SONG_ENDPOINT?id=$songId"))
        NetworkModule.provideNetworkPreferences(context).setIdToDownload(songId)
        val extFileSave = NetworkModule.provideNetworkPreferences(context).getExtensionFile()
        val nameFileSave = NetworkModule.provideNetworkPreferences(context).getNameFile()
        request.setTitle("Descargando $nameFileSave")
        request.setMimeType("audio/mp3")
        request.setDescription("Downloading file")
        val nameFile = "$nameFileSave$extFileSave"
        request.setDestinationUri(Uri.fromFile(getPathSongs(nameFile)))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
    }

    private fun getNamesFromHeaders(nameHeaders: Set<String>, headers: Headers): String {
        nameHeaders.forEach { header ->
            if (header == "content-disposition") {
                val fileName = headers.get(header)!!.split("=")
                return if (fileName.size > 2) {
                    val tmp = fileName[2].split('=')[0]
                    decode(tmp.removeRange(0, 7), StandardCharsets.UTF_8.name())
                } else {
                    fileName[1].replace("\"", "")
                }
            }
        }
        return DEFAULT_NAME_FILE
    }

    private fun ResponseBody.downloadWithProgress(fileName: String): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Downloading(0))
        var deleteFile = true
        val file = UtilsEnvironment.saveFileInDevice(fileName)
        try {
            byteStream().use { input ->

                file.outputStream().use { output ->
                    val totalBytes = contentLength()
                    val data = ByteArray(8_192)
                    var progressByte = 0L
                    while (true) {
                        val bytes = input.read(data)
                        if (bytes == -1) break
                        output.write(data, 0, bytes)
                        progressByte += bytes
                        emit(DownloadStatus.Downloading(progress = ((progressByte / 100) / totalBytes).toInt()))
                    }

                    when {
                        progressByte < totalBytes -> throw  Exception("Error al descargar el archivo")
                        progressByte > totalBytes -> throw  Exception("Error al descargar el archivo")
                        else -> deleteFile = false
                    }

                }

            }
            emit(DownloadStatus.Downloaded(file.exists()))
        } finally {
            if (deleteFile) file.delete()
        }

    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    suspend fun finishedDownload(id: Int, listener: DownloadFinishedListener) {
        try {
            val resultSet = mService.finishedDownload(id)
            listener.onFinish(resultSet.isSuccessful)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun finishDownload() {

    }


}
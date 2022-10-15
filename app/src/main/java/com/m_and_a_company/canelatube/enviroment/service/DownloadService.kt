package com.m_and_a_company.canelatube.enviroment.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadSongListener
import com.m_and_a_company.canelatube.domain.repository.song.SongsRepository
import com.m_and_a_company.canelatube.enviroment.ID_NOTIFICATION_DOWNLOAD
import com.m_and_a_company.canelatube.enviroment.ID_NOTIFICATION_DOWNLOAD_SUCCESS
import com.m_and_a_company.canelatube.enviroment.UtilsEnvironment
import com.m_and_a_company.canelatube.ui.svdn.SVDN
import com.m_and_a_company.canelatube.usesCases.DownloadSongUseCase
import com.m_and_a_company.canelatube.usesCases.FinishedDownloadUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadService : Service(), DownloadSongListener, DownloadFinishedListener{

    private val notifyManager by lazy {
        (this@DownloadService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    private lateinit var repository : SongsRepository


    override fun onCreate() {
        super.onCreate()
        repository = SongsRepository(this@DownloadService)
        CoroutineScope(Dispatchers.IO).launch {
            downloadSong()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun downloadSong() {
        val message = DownloadSongUseCase(repository).invoke(SVDN.idSongDownload)
    }

    override fun onProgress(progress: Int) {
        notifyManager.notify(
            ID_NOTIFICATION_DOWNLOAD,
            UtilsEnvironment.buildDownloadNotification(this, notifyManager, progress)
        )
    }

    override fun onFinished(isDownload: Boolean) {
        notifyManager.cancel(ID_NOTIFICATION_DOWNLOAD)
        notifyManager.notify(
            ID_NOTIFICATION_DOWNLOAD_SUCCESS,
            UtilsEnvironment.buildSuccessNotification(this, notifyManager)
        )
        CoroutineScope(Dispatchers.IO).launch {
            FinishedDownloadUseCase(repository).execute(SVDN.idSongDownload, this@DownloadService)
        }
    }

    private fun runToastHandler(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFinish(result: Boolean) {
        stopSelf()
    }

}
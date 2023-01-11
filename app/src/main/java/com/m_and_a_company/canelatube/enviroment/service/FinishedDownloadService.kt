package com.m_and_a_company.canelatube.enviroment.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.m_and_a_company.canelatube.domain.network.NetworkModule
import com.m_and_a_company.canelatube.domain.network.exceptions.SongException
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.repository.song.SongsRepository
import com.m_and_a_company.canelatube.enviroment.Notifications
import com.m_and_a_company.canelatube.usesCases.FinishedDownloadUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FinishedDownloadService : Service(), DownloadFinishedListener {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            finishedDownload()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private suspend fun finishedDownload() {
        val idDownload = NetworkModule.provideNetworkPreferences(applicationContext).getIdToDownload()
        try {
            FinishedDownloadUseCase(SongsRepository(applicationContext)).execute(idDownload, this)
        } catch (e: SongException) {
            println(e)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onFinish(result: Boolean) {
        Handler(Looper.getMainLooper()).post {
            val notifications = Notifications(this)
            if(result) {
                notifications.createNotificationSuccessDownload("Descarga completada", "Se completo correctamente la descarga")
            }else{
                notifications.createNotificationErrorDownload("Lo sentimos", "Ocurrio un error en la descarga")
            }
        }
        stopSelf()

    }
}
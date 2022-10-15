package com.m_and_a_company.canelatube.enviroment.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.m_and_a_company.canelatube.domain.network.NetworkModule
import com.m_and_a_company.canelatube.domain.network.interfaces.DownloadFinishedListener
import com.m_and_a_company.canelatube.domain.repository.song.SongsRepository
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

    private suspend fun finishedDownload() {
        val idDownload = NetworkModule.provideNetworkPreferences(applicationContext).getIdToDownload()
        FinishedDownloadUseCase(SongsRepository(applicationContext)).execute(idDownload, this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onFinish(result: Boolean) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "Descarga Completada", Toast.LENGTH_SHORT).show()
        }
        stopSelf()
    }
}
package com.m_and_a_company.canelatube.enviroment.service

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.enviroment.DownloadBroadcast
import com.m_and_a_company.canelatube.enviroment.Notifications

class DownloadedListenerService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var downloadManagerBroadcast: BroadcastReceiver

    override fun onCreate() {
        downloadManagerBroadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                context?.let {
                    DownloadBroadcast(it).downloadFished(intent)
                }
                stopForeground(true)
                stopSelf()
            }

        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(this, Notifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.downloads_icon)
            .setContentTitle("Esperando descarga")
            .setContentText("Esperando a que la descarga finalice")
        registerReceiver(downloadManagerBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notificationBuilder.build())
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(downloadManagerBroadcast)
        super.onDestroy()
    }
}
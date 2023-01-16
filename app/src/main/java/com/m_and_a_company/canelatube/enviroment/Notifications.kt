package com.m_and_a_company.canelatube.enviroment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.m_and_a_company.canelatube.MainActivity
import com.m_and_a_company.canelatube.R

//TODO: agregar la notificacion del servicio en primer plano y refactorizar
class Notifications(private val ctx: Context){

    private val CHANNEL_ERROR = "Canela de error"
    private val CHANNEL_SUCCESS = "Canal de exito"
    private val CHANNEL_FOREGROUND = "Canal de servicio primario"
    private val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_SUCCESS_ID = "canela_success_noty"
        const val CHANNEL_FOREGROUND_ID = "canela_foreground"
        const val CHANNEL_ERROR_ID = "canela_error_noty"
        const val OPEN_DETAIL_DOWNLOADS = "com.m_and_a_company.canelatube.OPEN_DETAIL_DOWNLOADS_ACTIVITY"
    }

    fun createNotificationErrorDownload(title: String, desc: String) {
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ERROR_ID).apply {
            setSmallIcon(R.drawable.downloads_icon)
            setContentTitle(title)
            setContentText(desc)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
        }
        notificationManager.notify(101, notification.build())
    }

    fun createNotificationSuccessDownload(title: String, desc: String) {
        val intentMain = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = OPEN_DETAIL_DOWNLOADS
        }
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intentMain, PendingIntent.FLAG_IMMUTABLE or 0)
        val notification = NotificationCompat.Builder(ctx, CHANNEL_SUCCESS_ID).apply {
            setSmallIcon(R.drawable.downloads_icon)
            setContentTitle(title)
            setContentText(desc)
            priority = NotificationCompat.PRIORITY_HIGH
            setContentIntent(pendingIntent)
            //TODO: Remover en la version 1.0
            //setAutoCancel(true)
        }
        notificationManager.notify(101, notification.build())
    }

    fun createChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelSuccess = NotificationChannel(CHANNEL_SUCCESS_ID, CHANNEL_SUCCESS, NotificationManager.IMPORTANCE_HIGH)
            val channelError = NotificationChannel(CHANNEL_ERROR_ID, CHANNEL_ERROR, NotificationManager.IMPORTANCE_HIGH)
            val channelForeground = NotificationChannel(CHANNEL_FOREGROUND_ID, CHANNEL_FOREGROUND, NotificationManager.IMPORTANCE_MIN)
            notificationManager.createNotificationChannel(channelSuccess)
            notificationManager.createNotificationChannel(channelError)
            notificationManager.createNotificationChannel(channelForeground)
        }
    }

}
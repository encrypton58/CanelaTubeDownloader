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

class Notifications(private val ctx: Context){

    private val CHANNEL_ID = "canela_error_noty"
    private val CHANNEL_NAME = "Canela Error Notification"
    private val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val OPEN_DETAIL_DOWNLOADS = "com.m_and_a_company.canelatube.OPEN_DETAIL_DOWNLOADS_ACTIVITY"
    }

    fun createNotificationErrorDownload(title: String, desc: String) {
        createChannelNotification()
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.downloads_icon)
            setContentTitle(title)
            setContentText(desc)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
        }
        notificationManager.notify(101, notification.build())
    }

    fun createNotificationSuccessDownload(title: String, desc: String) {
        createChannelNotification()
        val intentMain = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = OPEN_DETAIL_DOWNLOADS
        }
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intentMain, 0)
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.downloads_icon)
            setContentTitle(title)
            setContentText(desc)
            priority = NotificationCompat.PRIORITY_HIGH
            setContentIntent(pendingIntent)
            //setAutoCancel(true)
        }
        notificationManager.notify(101, notification.build())
    }

    private fun createChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

}
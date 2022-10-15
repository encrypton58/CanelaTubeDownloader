package com.m_and_a_company.canelatube.enviroment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.domain.network.PATH_DOWNLOAD
import java.io.File

object UtilsEnvironment {

     fun saveFileInDevice(fileName: String): File {
        val path = PATH_DOWNLOAD
        if(!PATH_DOWNLOAD.exists()){
            path.mkdir()
            return File(path, fileName)
        }
        return File("${PATH_DOWNLOAD.absolutePath}/$fileName")
     }

    fun buildDownloadNotification(
        context: Context,
        notifyManager: NotificationManager,
        progress: Int,
    ): Notification {
        val idChannel = createNotificationChannel(notifyManager)
        return NotificationCompat.Builder(context, idChannel)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.download_in_progress))
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()
    }

    fun buildSuccessNotification(context: Context, nm: NotificationManager): Notification {
        val idChannel = createNotificationChannel(nm)
        return NotificationCompat.Builder(context, idChannel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.download_success_title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel(nm: NotificationManager): String {
        val channelId = "Canela Tube Channel Notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            nm.createNotificationChannel(channel)
        }
        return channelId
    }

}
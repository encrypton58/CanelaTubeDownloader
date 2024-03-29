package com.m_and_a_company.canelatube.environment

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import com.m_and_a_company.canelatube.domain.network.NetworkModule
import com.m_and_a_company.canelatube.environment.service.FinishedDownloadService

//TODO: Refactorizar el codigo

class DownloadBroadcast(private val context: Context){

    private val preferences by lazy {
        NetworkModule.provideNetworkPreferences(context)
    }

     fun downloadFished(intent: Intent?) {
         val requiredDelete = preferences.getRequireCurrentDownloadDelete()
        if (intent != null && intent.action != null) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val validateErrorsInDownload = validateErrorInDownload(context, preferences.getIdFromDownloadManager())
                val notifications = Notifications(context)
                if (!validateErrorsInDownload.second) {
                    //TODO: Agregar texto desde el string resource
                    if(!requiredDelete) {
                        notifications.createNotificationSuccessDownload("Descarga completada", "Se Descargo la cancion exitosamente")
                    }
                } else {
                    //TODO: Agregar texto desde el string resource
                    notifications.createNotificationErrorDownload("Lo sentimos Hubo error", validateErrorsInDownload.first)
                }

                if (requiredDelete) {
                    context.startService(Intent(context, FinishedDownloadService::class.java))
                }

            }
        }
    }

    private fun validateErrorInDownload(ctx: Context, id: Long): Pair<String, Boolean> {
        try {
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val downloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)
            if (!cursor.moveToFirst()) {
                return Pair("", false)
            }
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            if (status != DownloadManager.STATUS_FAILED) {
                return Pair("", false)
            }
            val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
            cursor.close()
            return when (reason) {
                DownloadManager.ERROR_FILE_ERROR, DownloadManager.ERROR_CANNOT_RESUME, DownloadManager.ERROR_DEVICE_NOT_FOUND,
                DownloadManager.ERROR_UNKNOWN, DownloadManager.ERROR_HTTP_DATA_ERROR, DownloadManager.ERROR_UNHANDLED_HTTP_CODE,
                DownloadManager.ERROR_FILE_ALREADY_EXISTS, DownloadManager.ERROR_INSUFFICIENT_SPACE -> Pair(
                    "Ocurrio un error, code: $reason",
                    true
                )
                404 -> Pair("No se encontro la cancion", true)
                else -> Pair("Ocurrio un error", true)
            }
        } catch (e: java.lang.Exception) {
            return Pair("${e.message}", true)
        }
    }

}
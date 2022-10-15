package com.m_and_a_company.canelatube.enviroment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.m_and_a_company.canelatube.enviroment.service.FinishedDownloadService

class DownloadBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action != null) {
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    context!!.startService(Intent(context, FinishedDownloadService::class.java))
                }
            }
        }
    }

}
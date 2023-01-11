package com.m_and_a_company.canelatube

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import com.m_and_a_company.canelatube.domain.network.BASE_URL
import com.m_and_a_company.canelatube.enviroment.DownloadBroadcast

fun setReceiverDownload(context: Context, requireDelete: Boolean, idDownload: Long) {
    val downloadBroadcast = DownloadBroadcast(requireDelete, idDownload)
    context.registerReceiver(downloadBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}

fun getDownloadManager(context: Context): DownloadManager{
    return context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
}

fun getRequestDownload(idSong: Int): Request{
    return Request(Uri.parse("${BASE_URL}music/download/$idSong"))
}
package com.m_and_a_company.canelatube

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.net.Uri
import com.m_and_a_company.canelatube.domain.network.BASE_URL



fun getDownloadManager(context: Context): DownloadManager{
    return context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
}

fun getRequestDownload(idSong: Int): Request{
    return Request(Uri.parse("${BASE_URL}music/download/$idSong"))
}

fun getRequestOnichan(url: String) =Request(Uri.parse("${BASE_URL}$url"))
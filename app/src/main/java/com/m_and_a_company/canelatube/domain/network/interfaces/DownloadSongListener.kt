package com.m_and_a_company.canelatube.domain.network.interfaces

interface DownloadSongListener {

    fun onProgress(progress: Int)

    fun onFinished(isDownload: Boolean)

}
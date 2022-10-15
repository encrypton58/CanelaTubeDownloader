package com.m_and_a_company.canelatube.enviroment.service

sealed class DownloadStatus{
    data class Downloading(val progress: Int) : DownloadStatus()
    data class Downloaded(val isSave: Boolean) : DownloadStatus()
}

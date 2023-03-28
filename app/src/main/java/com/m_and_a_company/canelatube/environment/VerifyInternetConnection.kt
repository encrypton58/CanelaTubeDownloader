package com.m_and_a_company.canelatube.environment

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

fun deviceHaveInternetConnection(app: Application): Boolean {
    val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if(capabilities != null){
            if(capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)){
                return true
            } else if(capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)){
                return true
            }else if(capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)){
                return true
            }
        }
        return false
    }else{
        val conductivityManager =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conductivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
package com.m_and_a_company.canelatube.domain.network

import android.content.Context
import com.google.gson.Gson
import okhttp3.ResponseBody

object NetworkModule {

    fun <T> parseErrorResponse(response: ResponseBody?, type: Class<T>) : T? {
        response?.let { responseBody ->
            return Gson().fromJson(response.string(), type)
        }
        return null
    }

    fun provideNetworkPreferences(ctx: Context): NetworkPreferences {
        return NetworkPreferences(context = ctx)
    }

}

class NetworkPreferences(context: Context) {

    private val NAME_NETWORK_PREFERENCES = "network_preferences"

    private val preferences = context.getSharedPreferences(NAME_NETWORK_PREFERENCES, Context.MODE_PRIVATE)

    fun saveNameFile(value: String) {
        preferences.edit().putString(KEY_NAME_FILE_DOWNLOAD, value).apply()
    }

    fun saveExtensionFile(value: String) {
        preferences.edit().putString(KEY_EXTENSION_FILE_DOWNLOAD, value).apply()
    }

    fun getExtensionFile(): String {
        return preferences.getString(KEY_EXTENSION_FILE_DOWNLOAD, "") ?: ""
    }

    fun getNameFile(): String {
        return preferences.getString(KEY_NAME_FILE_DOWNLOAD, "") ?: ""
    }

    fun setIdToDownload(value: Int) {
        preferences.edit().putInt(KEY_ID_TO_DOWNLOAD, value).apply()
    }

    fun getIdToDownload(): Int {
        return preferences.getInt(KEY_ID_TO_DOWNLOAD, 0)
    }

    companion object {
        private const val KEY_NAME_FILE_DOWNLOAD = "name_file"
        private const val KEY_EXTENSION_FILE_DOWNLOAD = "extension_file"
        private const val KEY_ID_TO_DOWNLOAD = "id_to_download"
    }

}

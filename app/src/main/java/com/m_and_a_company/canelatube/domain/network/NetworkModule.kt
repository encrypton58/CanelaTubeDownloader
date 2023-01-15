package com.m_and_a_company.canelatube.domain.network

import android.content.Context
import com.google.gson.Gson
import com.m_and_a_company.canelatube.domain.network.model.Song
import okhttp3.ResponseBody
import org.json.JSONObject

object NetworkModule {

    fun <T> parseErrorResponse(response: ResponseBody?, type: Class<T>): T? {
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

    private val preferences =
        context.getSharedPreferences(NAME_NETWORK_PREFERENCES, Context.MODE_PRIVATE)

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

    fun generateKeyNamesSongsList(id: Int): String {
        return "name_song_${id}"
    }

    fun saveJsonDataGetSong(song: Song) {
        val songInJson = JSONObject().apply {
            put("id", song.id)
            put("name", song.name)
            put("size", song.size)
            put("image", song.image)
            put("ext", song.ext)
        }
        preferences.edit().putString(generateKeyNamesSongsList(song.id), songInJson.toString()).apply()
    }

    fun getJsonDataGetSong(id: Int): Song {
        val songJson = JSONObject(preferences.getString(generateKeyNamesSongsList(id), "").toString())
        val song = Song(
            id = songJson.getInt("id"),
            name = songJson.getString("name"),
            size = songJson.getString("size"),
            image = songJson.getString("image"),
            ext = songJson.getString("ext")
        )
        return song
    }

    fun setRequireCurrentDownloadDelete(requireDelete: Boolean) {
        preferences.edit().putBoolean(KEY_REQUIRE_DELETE, requireDelete).apply()
    }

    fun getRequireCurrentDownloadDelete(): Boolean {
        return preferences.getBoolean(KEY_REQUIRE_DELETE, false)
    }

    fun setIdFromDownloadManager(idDownload: Long) {
        preferences.edit().putLong(KEY_ID_DOWN_MANAGER, idDownload).apply()
    }

    fun getIdFromDownloadManager(): Long = preferences.getLong(KEY_ID_DOWN_MANAGER, 0)

    companion object {
        private const val KEY_NAME_FILE_DOWNLOAD = "name_file"
        private const val KEY_EXTENSION_FILE_DOWNLOAD = "extension_file"
        private const val KEY_ID_TO_DOWNLOAD = "id_to_download"
        private const val KEY_REQUIRE_DELETE = "require_delete"
        private const val KEY_ID_DOWN_MANAGER = "id_down_manager"
    }

}

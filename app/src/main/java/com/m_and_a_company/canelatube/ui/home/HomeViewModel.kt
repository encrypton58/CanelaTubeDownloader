package com.m_and_a_company.canelatube.ui.home

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore.Audio.Media
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.m_and_a_company.canelatube.domain.data.models.SongDownloaded
import com.m_and_a_company.canelatube.environment.PATH_SEARCH_lLOCAL_SONGS
import com.m_and_a_company.canelatube.environment.isUpApi29
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState
import wseemann.media.FFmpegMediaMetadataRetriever

class HomeViewModel : ViewModel() {

    private val _state = MutableLiveData<DownloadUIState>()
    private val _permissions = MutableLiveData<Boolean>()
    private val _songsDownloaded = MutableLiveData<List<SongDownloaded>>()

    val statePermission : LiveData<Boolean> = _permissions
    val state: LiveData<DownloadUIState> = _state
    val songsDownloaded: LiveData<List<SongDownloaded>> = _songsDownloaded

    fun removeItem(pos: Int) {
        _songsDownloaded.value?.let { songsDownloaded ->
            val mutableList = mutableListOf<SongDownloaded>()
            for (song in songsDownloaded) {
                mutableList.add(song)
            }
            mutableList.removeAt(pos)
            _songsDownloaded.postValue(mutableList)
        }
    }

    fun getFilesDownloaded(contentResolver: ContentResolver) {
        val songsLocal = ArrayList<SongDownloaded>()
        val path = Media.EXTERNAL_CONTENT_URI
        val cursorColumns = arrayOf(
            Media._ID,
            Media.TITLE,
            Media.ARTIST,
            Media.DATA
        )
        val shortOrder = "${Media.DATE_MODIFIED} DESC"
        val where = "${Media.IS_MUSIC} != 0 AND ${Media.DATA} LIKE '${PATH_SEARCH_lLOCAL_SONGS}'"
        contentResolver.query(
            path,
            cursorColumns,
            where,
            null,
            shortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID))
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                val uriSong =
                    ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)

                if(isUpApi29()) {
                    val metadata = MediaMetadataRetriever()
                    metadata.setDataSource(data)
                    val byte = metadata.embeddedPicture
                    var bitmap: Bitmap? = null
                    if(byte != null) {
                        metadata.release()
                       bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
                    }
                    songsLocal.add(SongDownloaded(id, title, artist, bitmap, uriSong, data))
                }else{
                    val metadata = FFmpegMediaMetadataRetriever()
                    metadata.setDataSource(data)
                    val byte = metadata.embeddedPicture
                    val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
                    songsLocal.add(SongDownloaded(id, artist, title, bitmap, uriSong, data))
                    metadata.release()
                }
            }
        }
        _songsDownloaded.postValue(songsLocal)
    }

    fun hasSongsDownloaded(resolver: ContentResolver): Boolean {
        val songsLocal = ArrayList<Long>()
        val path = Media.EXTERNAL_CONTENT_URI
        val cursorColumns = arrayOf(
            Media._ID,
        )
        val where = "${Media.IS_MUSIC} != 0 AND ${Media.DATA} LIKE '${PATH_SEARCH_lLOCAL_SONGS}'"
        resolver.query(
            path,
            cursorColumns,
            where,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID))
                songsLocal.add(id)
            }
        }
        return songsLocal.isNotEmpty()
    }

    fun clearState() {
        _state.postValue(DownloadUIState.ClearState)
    }

}
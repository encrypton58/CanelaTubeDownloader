package com.m_and_a_company.canelatube.domain.network.interfaces

import com.m_and_a_company.canelatube.core.daison.ResultSet
import com.m_and_a_company.canelatube.domain.network.ID_URL_PARAM
import com.m_and_a_company.canelatube.domain.network.ITAG_PARAM
import com.m_and_a_company.canelatube.domain.network.PREFIX_DELETE_SONG_DOWNLOAD
import com.m_and_a_company.canelatube.domain.network.PREFIX_GET_FORMATS_URL
import com.m_and_a_company.canelatube.domain.network.PREFIX_GET_SONGS_URL
import com.m_and_a_company.canelatube.domain.network.URL_PARAM
import com.m_and_a_company.canelatube.domain.network.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.domain.network.model.ResponseApi
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.domain.network.model.SongDeleteModel
import com.m_and_a_company.canelatube.domain.network.model.SongIdModel
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicService {

    @GET(PREFIX_GET_SONGS_URL)
    suspend fun getSongs(): Response<ResponseApi.Success<List<Song>>>

    @GET(PREFIX_GET_FORMATS_URL)
    suspend fun getMusic(@Query(URL_PARAM) url: String): Response<ResultSet<MusicDownloadsModel>>

    @POST(PREFIX_GET_FORMATS_URL)
    @FormUrlEncoded
    suspend fun getSongId(
        @Field(URL_PARAM) url: String,
        @Field(ITAG_PARAM) songId: Int
    ): Response<ResponseApi.Success<SongIdModel>>

    @DELETE(PREFIX_DELETE_SONG_DOWNLOAD)
    suspend fun deleteSong(@Path(ID_URL_PARAM) id: Int): Response<ResponseApi.Success<SongDeleteModel>>

}
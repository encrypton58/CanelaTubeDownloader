package com.m_and_a_company.canelatube.network.interfaces

import com.m_and_a_company.canelatube.network.domain.model.MusicDownloadsModel
import com.m_and_a_company.canelatube.network.domain.model.ResponseApi
import com.m_and_a_company.canelatube.network.domain.model.SongDeleteModel
import com.m_and_a_company.canelatube.network.domain.model.SongIdModel
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface MusicService {

    @GET("music")
    suspend fun getMusic(@Query("url") url: String): Response<ResponseApi.Success<MusicDownloadsModel>>

    @POST("music")
    @FormUrlEncoded
    suspend fun getSongId(
        @Field("url") url: String,
        @Field("itag") songId: Int
    ): Response<ResponseApi.Success<SongIdModel>>

    @Streaming
    @GET("music/download/{id}")
    suspend fun downloadSong(@Path("id") id: Int): Response<ResponseApi.Success<Boolean>>

    @DELETE("music/{id}")
    suspend fun deleteSong(@Path("id") id: Int): Response<ResponseApi.Success<SongDeleteModel>>

}
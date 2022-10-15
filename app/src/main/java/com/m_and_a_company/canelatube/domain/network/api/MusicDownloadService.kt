package com.m_and_a_company.canelatube.domain.network.api

import com.m_and_a_company.canelatube.domain.data.models.SongInfo
import com.m_and_a_company.canelatube.domain.data.models.VideoInfo
import com.m_and_a_company.canelatube.domain.network.*
import com.m_and_a_company.canelatube.domain.network.model.ResponseFromConvert
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MusicDownloadService {

    @GET(GET_SONG_INFO)
    suspend fun infoSongFromUrl(@Query(GET_INFO_ENDPOINT_KEY_QUERY_PARAM) url: String): Response<SongInfo>

    @GET(GET_VIDEO_INFO)
    suspend fun infoVideoFromUrl(@Query(GET_INFO_ENDPOINT_KEY_QUERY_PARAM) url: String): Response<VideoInfo>

    @POST("$POST_PREPARE_PREFIX/{typeDownload}")
    @FormUrlEncoded
    suspend fun prepareSong(
        @Path("typeDownload") type: String,
        @Field(PREPARE_SONG_KEY_URL_PARAM) url: String,
        @Field(PREPARE_SONG_KEY_ITAG_PARAM) iTag: String
        ): Response<ResponseFromConvert>

    @Streaming
    @GET(DOWNLOAD_SONG_ENDPOINT)
    suspend fun downloadSong(@Query(DOWNLOAD_SONG_QUERY_PARAM) songId: Int): Response<ResponseBody>

    @POST(DELETE_ENDPOINT)
    @FormUrlEncoded
    suspend fun finishedDownload(@Field(DOWNLOAD_SONG_QUERY_PARAM) idToDelete: Int): Response<ResponseBody>

    @POST("$POST_PREPARE_PREFIX/")
    @FormUrlEncoded
    suspend fun prepareVideo(
        @Field(PREPARE_SONG_KEY_URL_PARAM) url: String,
        @Field(PREPARE_SONG_KEY_ITAG_PARAM) iTag: String
    ): Response<ResponseFromConvert>

}
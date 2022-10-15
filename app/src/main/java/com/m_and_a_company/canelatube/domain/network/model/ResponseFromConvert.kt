package com.m_and_a_company.canelatube.domain.network.model

import com.google.gson.annotations.SerializedName

data class ResponseFromConvert(
    val idDownload: Int,
    @SerializedName("name") val nameFile: String,
    @SerializedName("ext") val extensionFile: String
)

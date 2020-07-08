package com.dreamsunited.newsapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

const val ERROR_STATUS = "error"

const val SUCCESS_STATUS = "ok"

@Parcelize
data class NewsApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResult: Int?,
    @SerializedName("articles") val articles: List<Articles>?,
    @SerializedName("sources") val sources: List<Source>?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: String?
) : Parcelable


data class ApiError(val code: String, val msg: String) : Throwable()

fun NewsApiResponse.mapThrowing(): NewsApiResponse {
    if (status == SUCCESS_STATUS) {
        return this
    }
    if (status == ERROR_STATUS) {
        throw ApiError(this.code!!, this.message!!)
    }
    throw Throwable("Unknown Error")
}
package com.dreamsunited.newsapp.model

import android.os.Parcelable
import com.dreamsunited.newsapp.utils.changeDateFormat
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by ak93.droid@gmail.com on 04,June,2020
 */
@Parcelize
data class Articles(
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val imageUrl: String,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("source") val source: Source,
    @SerializedName("content") val content: String?
) : Parcelable {
    fun getDisplayDate() = changeDateFormat(publishedAt)
}
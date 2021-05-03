package com.rpandey.covid19tracker_india.data.model.covidIndia.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NewsListResponse constructor(
    @SerializedName("category") val category: String,
    @SerializedName("data") val data: List<NewsData>?,
    @SerializedName("success") val success: Boolean
)
@Keep
data class NewsData constructor(
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("readMoreUrl") val readMoreUrl: String?
)
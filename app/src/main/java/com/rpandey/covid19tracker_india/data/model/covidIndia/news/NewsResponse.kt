package com.rpandey.covid19tracker_india.data.model.covidIndia.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NewsResponse(
    @SerializedName("source") val source: String?,
    @SerializedName("headlines") val headlines: List<String>,
    @SerializedName("headlines_summary") val summary: List<String>?,
    @SerializedName("image_link") val images: List<String>?
)
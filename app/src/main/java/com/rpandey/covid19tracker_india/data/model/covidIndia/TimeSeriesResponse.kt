package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class TimeSeriesResponse {
    @SerializedName("delta")
    val delta: Data? = null
    @SerializedName("total")
    val total: Data? = null
}
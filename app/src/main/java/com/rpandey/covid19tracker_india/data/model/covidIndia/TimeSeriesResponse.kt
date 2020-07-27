package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class TimeSeriesResponse {
    @SerializedName("dates")
    val dates: LinkedHashMap<String, TimeSeriesData>? = null
}

@Keep
class TimeSeriesData {
    @SerializedName("delta")
    val delta: Data? = null
    @SerializedName("total")
    val total: Data? = null
}
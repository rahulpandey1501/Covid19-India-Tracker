package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
enum class Zone {
    @SerializedName("Red") Red,
    @SerializedName("Green") Green,
    @SerializedName("Orange") Orange
}
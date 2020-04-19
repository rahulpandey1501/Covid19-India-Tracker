package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class DailyStateResponse {

    @SerializedName("states_daily")
    val stateDaily: List<MutableMap<String, String>>? = null
}

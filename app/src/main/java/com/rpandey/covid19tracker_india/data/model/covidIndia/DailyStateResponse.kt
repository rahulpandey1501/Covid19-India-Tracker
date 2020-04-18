package com.rpandey.covid19tracker_india.data.model.covidIndia

import com.google.gson.annotations.SerializedName

class DailyStateResponse {

    @SerializedName("states_daily")
    val stateDaily: List<MutableMap<String, String>>? = null
}

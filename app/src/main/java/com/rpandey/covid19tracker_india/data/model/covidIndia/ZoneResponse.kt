package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class ZoneResponse(
    @SerializedName("zones") val zones: List<ZoneData>
)

@Keep
class ZoneData(
    @SerializedName("district") val district: String,
    @SerializedName("state") val stateName: String,
    @SerializedName("statecode") val stateCode: String,
    @SerializedName("zone") val zone: String
)
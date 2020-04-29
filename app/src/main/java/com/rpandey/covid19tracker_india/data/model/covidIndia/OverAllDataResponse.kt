package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class OverAllDataResponse {
    @SerializedName("statewise")
    val stateData: List<StateData>? = null
    @SerializedName("tested")
    val testData: List<OverAllTestData>? = null
}

@Keep
data class OverAllTestData(
    @SerializedName("totalsamplestested") val totalTested: String,
    @SerializedName("updatetimestamp") val date: String // "dd/MM/yyyy hh:mm:ss"
)

@Keep
data class StateData(
    @SerializedName("active") val active: Int,
    @SerializedName("confirmed") val confirmed: Int,
    @SerializedName("deaths") val deaths: Int,
    @SerializedName("deltaconfirmed") val deltaConfirmed: Int,
    @SerializedName("deltadeaths") val deltaDeaths: Int,
    @SerializedName("deltarecovered") val deltaRecovered: Int,
    @SerializedName("lastupdatedtime") val lastUpdatedTime: String,
    @SerializedName("recovered") val recovered: Int,
    @SerializedName("state") val stateName: String,
    @SerializedName("statecode") val stateCode: String
)
package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class DistrictResponse {

    @SerializedName("state")
    val stateName: String? = null

    @SerializedName("statecode")
    val stateCode: String? = null

    @SerializedName("districtData")
    val districtData: List<DistrictData>? = null
}

@Keep
class DistrictData(

    @SerializedName("district")
    val district: String,

    @SerializedName("confirmed")
    val confirmed: Int,

    @SerializedName("deceased")
    val deceased: Int,

    @SerializedName("recovered")
    val recovered: Int,

    @SerializedName("delta")
    val delta: Delta

)

@Keep
class Delta(
    @SerializedName("confirmed")
    val confirmed: Int,

    @SerializedName("deceased")
    val deceased: Int,

    @SerializedName("recovered")
    val recovered: Int

)
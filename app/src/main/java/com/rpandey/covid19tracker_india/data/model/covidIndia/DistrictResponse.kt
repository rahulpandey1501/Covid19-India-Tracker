package com.rpandey.covid19tracker_india.data.model.covidIndia

import com.google.gson.annotations.SerializedName

class DistrictResponse {

    @SerializedName("state")
    val state: String? = null

    @SerializedName("districtData")
    val districtData: List<DistrictData>? = null
}

class DistrictData(

    @SerializedName("district")
    val district: String,

    @SerializedName("confirmed")
    val confirmed: Int

)
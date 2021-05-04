package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class OverAllDataResponse {
    @SerializedName("districts")
    val districts: HashMap<String, DistrictData>? = null
    @SerializedName("meta")
    val meta: Meta? = null
    @SerializedName("delta")
    val delta: Data? = null
    @SerializedName("total")
    val total: Data? = null
}

@Keep
class Meta {
    @SerializedName("last_updated")
    val lastUpdated: String? = null
    @SerializedName("tested")
    val tested: Tested? = null
    @SerializedName("population")
    val population: Long? = null
}

@Keep
class Tested {
    @SerializedName("last_updated")
    val lastUpdated: String? = null
}

@Keep
class Data {
    @SerializedName("confirmed")
    val confirmed: Int = 0
    @SerializedName("recovered")
    val recovered: Int = 0
    @SerializedName("deceased")
    val deceased: Int = 0
    @SerializedName("tested")
    val tested: Int = 0
    @SerializedName("vaccinated")
    val vaccinated: Int = 0

    fun getActive(): Int = confirmed - recovered - deceased
}

@Keep
class DistrictData {
    @SerializedName("delta")
    val delta: Data? = null
    @SerializedName("total")
    val total: Data? = null
    @SerializedName("meta")
    val meta: Meta? = null
}
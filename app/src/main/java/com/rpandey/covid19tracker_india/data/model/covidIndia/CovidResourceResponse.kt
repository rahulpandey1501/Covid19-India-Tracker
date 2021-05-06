package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CovidResourceResponse(
    @SerializedName("crowdsourcd_resources_links") val data: List<CovidResourceData>?
)

@Keep
class CovidResourceData(
    @SerializedName("link") val link: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("shoulddisplay") val shoulddisplay: String = "Yes"
)
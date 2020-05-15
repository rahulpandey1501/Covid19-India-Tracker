package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class ResourceResponse(
    @SerializedName("resources") val resources: List<ResourceData>
)

@Keep
class ResourceData(
    @SerializedName("recordid") val recordid: String,
    @SerializedName("category") val category: String,
    @SerializedName("city") val district: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("phonenumber") val phoneNumber: String?,
    @SerializedName("descriptionandorserviceprovided") val description: String,
    @SerializedName("nameoftheorganisation") val organisation: String,
    @SerializedName("state") val state: String
)
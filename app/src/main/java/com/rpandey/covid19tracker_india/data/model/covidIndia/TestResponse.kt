package com.rpandey.covid19tracker_india.data.model.covidIndia

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class TestResponse(
    @SerializedName("states_tested_data") val testData: List<TestData>
)

@Keep
class TestData(
    @SerializedName("state") val stateName: String,
    @SerializedName("totaltested") val totalTest: String?,
    @SerializedName("updatedon") val date: String,
    @Transient var timestamp: Long // local construct
)
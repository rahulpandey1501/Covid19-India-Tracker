package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.model.covidIndia.DailyStateResponse
import com.rpandey.covid19tracker_india.data.model.covidIndia.DistrictResponse
import com.rpandey.covid19tracker_india.data.model.covidIndia.OverAllDataResponse
import com.rpandey.covid19tracker_india.data.model.covidIndia.TestResponse
import retrofit2.http.GET

interface CovidIndiaApiService {

    companion object {
        const val BASE_URL = "https://api.covid19india.org/"
    }

    @GET("data.json")
    suspend fun getOverAllData(): OverAllDataResponse

    @GET("states_daily.json")
    suspend fun getStateData(): DailyStateResponse

    @GET("v2/state_district_wise.json")
    suspend fun getDistrictData(): List<DistrictResponse>

    @GET("state_test_data.json")
    suspend fun getTestingData(): TestResponse
}
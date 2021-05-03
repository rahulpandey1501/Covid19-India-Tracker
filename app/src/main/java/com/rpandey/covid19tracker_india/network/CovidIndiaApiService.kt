package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.model.covidIndia.*
import com.rpandey.covid19tracker_india.data.model.covidIndia.news.NewsListResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface CovidIndiaApiService {

    companion object {
        const val BASE_URL = "https://api.covid19india.org/"
    }

    @GET("v4/data.json")
    suspend fun getOverAllData(): HashMap<String, OverAllDataResponse>

    @GET("zones.json")
    suspend fun getZoneData(): ZoneResponse

    @GET("resources/resources.json")
    suspend fun getResources(): ResourceResponse

    @GET("v4/timeseries.json")
    suspend fun getTimeSeries(): HashMap<String, TimeSeriesResponse>

    @GET
    suspend fun getNews(@Url url: String): NewsListResponse
}
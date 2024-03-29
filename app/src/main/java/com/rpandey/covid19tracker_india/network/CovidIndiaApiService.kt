package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.model.covidIndia.*
import com.rpandey.covid19tracker_india.data.model.covidIndia.news.NewsListResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface CovidIndiaApiService {

    companion object {
        const val BASE_URL = "https://data.incovid19.org/"
    }

    @GET("v4/min/data.min.json")
    suspend fun getOverAllData(): HashMap<String, OverAllDataResponse>

    @GET("zones.json")
    suspend fun getZoneData(): ZoneResponse

    @GET("resources/resources.json")
    suspend fun getResources(): ResourceResponse

    @GET("crowdsourced_resources_links.json")
    suspend fun getCovidResources(): CovidResourceResponse

    @GET("v4/mini/timeseries.min.json")
    suspend fun getTimeSeries(): HashMap<String, TimeSeriesResponse>

    @GET
    suspend fun getNews(@Url url: String): NewsListResponse
}
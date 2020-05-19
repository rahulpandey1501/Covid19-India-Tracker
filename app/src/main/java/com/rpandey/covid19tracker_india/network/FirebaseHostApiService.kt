package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.data.model.LaunchData
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FirebaseHostApiService {

    companion object {
        const val BASE_URL = "https://covid19-india-tracker-fad5a.web.app/"
    }

    @GET("app_launch_data/data.json")
    suspend fun launchPayload(): LaunchData

    @Streaming
    @GET
    suspend fun downloadFile(@Url downloadUrl: String): ResponseBody
}
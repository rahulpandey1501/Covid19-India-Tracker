package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkBuilder {

    private fun getRetrofit(baseUrl: String): Retrofit {

        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build() //Doesn't require the adapter
    }

    val apiService: CovidIndiaApiService = getRetrofit(CovidIndiaApiService.BASE_URL).create(CovidIndiaApiService::class.java)

}
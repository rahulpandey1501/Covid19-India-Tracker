package com.rpandey.covid19tracker_india.network

import com.rpandey.covid19tracker_india.BuildConfig
import com.rpandey.covid19tracker_india.CovidApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkBuilder {

    private val cache = Cache(CovidApplication.INSTANCE.cacheDir, 10 * 1024 * 1024) // 10mb

    private fun getRetrofit(baseUrl: String): Retrofit {

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(logging)
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build() //Doesn't require the adapter
    }

    val apiService: CovidIndiaApiService = getRetrofit(CovidIndiaApiService.BASE_URL).create(CovidIndiaApiService::class.java)

    val firebaseHostService: FirebaseHostApiService = getRetrofit(FirebaseHostApiService.BASE_URL).create(FirebaseHostApiService::class.java)

}
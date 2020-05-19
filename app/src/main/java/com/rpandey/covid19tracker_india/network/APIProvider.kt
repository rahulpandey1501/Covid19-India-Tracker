package com.rpandey.covid19tracker_india.network

class APIProvider private constructor(
    val covidIndia: CovidIndiaApiService,
    val firebaseHostApiService: FirebaseHostApiService) {

    companion object {
        private lateinit var INSTANCE: APIProvider

        fun getInstance(): APIProvider {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = APIProvider(
                    NetworkBuilder.apiService,
                    NetworkBuilder.firebaseHostService
                )
            }
            return INSTANCE
        }
    }
}
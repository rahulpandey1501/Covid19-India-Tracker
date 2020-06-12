package com.rpandey.covid19tracker_india.data.processor

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import com.rpandey.covid19tracker_india.network.CovidIndiaApiService
import com.rpandey.covid19tracker_india.network.FirebaseHostApiService
import com.rpandey.covid19tracker_india.util.PreferenceHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CovidIndiaSyncManager(
    private val apiProvider: APIProvider,
    private val covidDatabase: CovidDatabase) {

    private val zoneProcessor by lazy { ZoneProcessor(covidDatabase) }
    private val overallDataProcessor by lazy { OverallDataProcessor(covidDatabase) }
    private val timeSeriesDataProcessor by lazy { TimeSeriesDataProcessor(covidDatabase) }
    private val resourceDataProcessor by lazy { ResourceDataProcessor(covidDatabase) }
    private val newsDatProcessor by lazy { NewsDataProcessor(covidDatabase) }

    companion object {
        private lateinit var INSTANCE: CovidIndiaSyncManager
        fun getInstance(): CovidIndiaSyncManager {
            if (!::INSTANCE.isInitialized) {
                val apiProvider = APIProvider.getInstance()
                val covidDatabase = CovidDatabase.getInstance()
                INSTANCE = CovidIndiaSyncManager(apiProvider, covidDatabase)
            }

            return INSTANCE
        }
    }

    fun startSync(callback: suspend (Status<*>) -> Unit = {}) = CoroutineScope(Dispatchers.IO).launch {

        syncPrimaryData(callback)

        // Fetching app launch data
        launch {
            syncAppLaunchData(apiProvider.firebaseHostApiService, callback)
        }


        // Fetching app launch data
        launch {
            syncTimeSeriesData(apiProvider.covidIndia, callback)
        }

        // Fetching resources data information
        launch {
            val resourceStatus = ApiHelper.handleRequest(StatusId.RESOURCE_DATA) {
                apiProvider.covidIndia.getResources()
            }

            if (resourceStatus is Status.Success) {
                resourceDataProcessor.process(resourceStatus.data)
            }

            callback(resourceStatus)
        }

        // fetch news
        launch {
            syncNews(Country.INDIA)
        }
    }

    suspend fun syncPrimaryData(callback: suspend (Status<*>) -> Unit) {
        // Fetching overall primary data information
        val overallDataStatus = ApiHelper.handleRequest(StatusId.OVERALL_DATA) {
            apiProvider.covidIndia.getOverAllData()
        }

        if (overallDataStatus is Status.Success) {
            overallDataProcessor.process(overallDataStatus.data)
        }

        callback(overallDataStatus)

        // Fetching zonal information
        val zoneStatus = ApiHelper.handleRequest(StatusId.ZONE_DATA) {
            apiProvider.covidIndia.getZoneData()
        }

        if (zoneStatus is Status.Success) {
            zoneProcessor.process(zoneStatus.data.zones)
        }

        callback(zoneStatus)
    }

    suspend fun syncNews(country: Country) {
        val newsStatus = ApiHelper.handleRequest(StatusId.TODAYS_NEWS) {
            apiProvider.covidIndia.getNews(Constants.NEWS_URL)
        }

        if (newsStatus is Status.Success) {
            newsDatProcessor.process(newsStatus.data)
        }
    }

    private suspend fun syncAppLaunchData(service: FirebaseHostApiService, callback: suspend (Status<*>) -> Unit) {
        val status = ApiHelper.handleRequest(StatusId.LAUNCH_DATA) { service.launchPayload() }
        if (status is Status.Success) {
            status.data.config?.let { PreferenceHelper.putString(Constants.KEY_CONFIG, Gson().toJson(it)) }
            status.data.shareUrl?.let { PreferenceHelper.putString(Constants.KEY_SHARE_URL, it) }
        }
        callback(status)
    }

    private suspend fun syncTimeSeriesData(service: CovidIndiaApiService, callback: suspend (Status<*>) -> Unit) {
        val status = ApiHelper.handleRequest(StatusId.TIME_SERIES) { service.getTimeSeries() }
        if (status is Status.Success) {
            timeSeriesDataProcessor.process(status.data)
        }
        callback(status)
    }

    private val handler = CoroutineExceptionHandler { context, throwable ->
        FirebaseAnalytics.getInstance(CovidApplication.INSTANCE).logEvent("Network Exception: ${throwable.message}", null)
    }
}
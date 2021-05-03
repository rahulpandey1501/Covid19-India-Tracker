package com.rpandey.covid19tracker_india.data.processor

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.RequestId
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.OverAllDataResponse
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
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

    fun syncAllData(callback: suspend (Status<*>) -> Unit = {}) = CoroutineScope(Dispatchers.IO).launch {
        startSync(RequestId.values(), callback)
    }

    suspend fun startSync(requestList: Array<RequestId>, callback: suspend (Status<*>) -> Unit = {}) = CoroutineScope(Dispatchers.IO).launch {
        requestList.forEach { requestId ->
            when (requestId) {

                RequestId.OVERALL_DATA -> {
                    syncOverallData(callback)
                }

                RequestId.TIME_SERIES -> {
                    launch {
                        syncTimeSeriesData(callback)
                    }
                }

                RequestId.RESOURCE_DATA -> {
                    launch {
                        syncResources(callback)
                    }
                }

                RequestId.NEWS_DATA -> {
                    launch {
                        syncNews(Country.INDIA, callback)
                    }
                }

                RequestId.LAUNCH_DATA -> {
                    launch {
                        syncAppLaunchData(callback)
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun syncOverallData(callback: suspend (Status<HashMap<String, OverAllDataResponse>>) -> Unit) {
        ApiHelper().syncAndProcess(RequestId.OVERALL_DATA, {
            apiProvider.covidIndia.getOverAllData()
        }, callback, overallDataProcessor)
    }

    private suspend fun syncZone(callback: suspend (Status<*>) -> Unit) {
        ApiHelper().syncAndProcess(RequestId.ZONE_DATA, {
            apiProvider.covidIndia.getZoneData()
        }, callback, zoneProcessor)
    }

    private suspend fun syncResources(callback: suspend (Status<*>) -> Unit) {
        ApiHelper().syncAndProcess(RequestId.RESOURCE_DATA, {
            apiProvider.covidIndia.getResources()
        }, callback, resourceDataProcessor)
    }

    private suspend fun syncNews(country: Country, callback: suspend (Status<*>) -> Unit) {
        ApiHelper().syncAndProcess(RequestId.NEWS_DATA, {
            apiProvider.covidIndia.getNews(Constants.NEWS_URL)
        }, callback, newsDatProcessor)
    }

    private suspend fun syncTimeSeriesData(callback: suspend (Status<*>) -> Unit) {
        ApiHelper().syncAndProcess(RequestId.TIME_SERIES, {
            apiProvider.covidIndia.getTimeSeries()
        }, callback, timeSeriesDataProcessor)
    }

    private suspend fun syncAppLaunchData(callback: suspend (Status<*>) -> Unit) {
        val status = ApiHelper().handleRequest(RequestId.LAUNCH_DATA) {
            apiProvider.firebaseHostApiService.launchPayload()
        }

        if (status is Status.Success) {
            status.data.config?.let { PreferenceHelper.putString(Constants.KEY_CONFIG, Gson().toJson(it)) }
            status.data.shareUrl?.let { PreferenceHelper.putString(Constants.KEY_SHARE_URL, it) }
        }

        callback(status)
    }

    private val handler = CoroutineExceptionHandler { context, throwable ->
        FirebaseAnalytics.getInstance(CovidApplication.INSTANCE).logEvent("Network Exception: ${throwable.message}", null)
    }
}
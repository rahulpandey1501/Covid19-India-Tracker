package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import com.rpandey.covid19tracker_india.network.FirebaseHostApiService
import kotlinx.coroutines.*

class CovidIndiaDataProcessor(
    private val apiProvider: APIProvider,
    private val covidDatabase: CovidDatabase) {

    private val stateDataProcessor by lazy { DailyStateDataProcessor(covidDatabase) }
    private val districtDataProcessor by lazy { DistrictDataProcessor(covidDatabase) }
    private val overallDataProcessor by lazy { OverallDataProcessor(covidDatabase) }
    private val testDataProcessor by lazy { TestDataProcessor(covidDatabase) }

    suspend fun startSync(callback: (Status<*>) -> Unit) {

        syncAppLaunchData(apiProvider.firebaseHostApiService, callback)

        val status = ApiHelper.handleRequest(StatusId.OVERALL_DATA) { apiProvider.covidIndia.getOverAllData() }
        when (status) {
            is Status.Success -> overallDataProcessor.process(status.data)
        }
        callback(status)

        CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler {_,_ ->}).launch {
            val districtResponse = async { apiProvider.covidIndia.getDistrictData() }
            val testDataResponse = async { apiProvider.covidIndia.getTestingData() }

            when (val status = ApiHelper.handleRequest(StatusId.DISTRICT_DATA) { districtResponse.await() }) {
                is Status.Success -> districtDataProcessor.process(status.data)
            }

            when (val status = ApiHelper.handleRequest(StatusId.TESTING_DATA) { testDataResponse.await() }) {
                is Status.Success -> testDataProcessor.process(status.data)
            }
        }
    }

    private suspend fun syncAppLaunchData(service: FirebaseHostApiService, callback: (Status<*>) -> Unit) {
        val status = ApiHelper.handleRequest(StatusId.LAUNCH_DATA) { service.launchPayload() }
        callback(status)
    }
}
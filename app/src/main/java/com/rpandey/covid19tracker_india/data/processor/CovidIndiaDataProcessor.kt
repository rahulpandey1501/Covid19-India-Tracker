package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.model.covidIndia.ZoneResponse
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import com.rpandey.covid19tracker_india.network.FirebaseHostApiService
import com.rpandey.covid19tracker_india.util.PreferenceHelper

class CovidIndiaDataProcessor(
    private val apiProvider: APIProvider,
    private val covidDatabase: CovidDatabase) {

    private val stateDataProcessor by lazy { DailyStateDataProcessor(covidDatabase) }
    private val districtDataProcessor by lazy { DistrictDataProcessor(covidDatabase) }
    private val overallDataProcessor by lazy { OverallDataProcessor(covidDatabase) }
    private val testDataProcessor by lazy { TestDataProcessor(covidDatabase) }

    suspend fun startSync(callback: (Status<*>) -> Unit) {

        syncAppLaunchData(apiProvider.firebaseHostApiService, callback)

        val overallDataStatus = ApiHelper.handleRequest(StatusId.OVERALL_DATA) {
            apiProvider.covidIndia.getOverAllData()
        }

        if (overallDataStatus is Status.Success) {
            overallDataProcessor.process(overallDataStatus.data)
        }

        callback(overallDataStatus)

        val districtStatus = ApiHelper.handleRequest(StatusId.DISTRICT_DATA) {
            apiProvider.covidIndia.getDistrictData()
        }

        val zoneStatus = ApiHelper.handleRequest(StatusId.ZONE_DATA) {
            apiProvider.covidIndia.getZoneData()
        }

        if (districtStatus is Status.Success) {
            val districtData = districtStatus.data
            var zoneData: ZoneResponse? = null
            if (zoneStatus is Status.Success)
                zoneData = zoneStatus.data

            districtDataProcessor.process(districtData to (zoneData?.zones ?: emptyList()))
        }

        val testingStatus = ApiHelper.handleRequest(StatusId.TESTING_DATA) {
            apiProvider.covidIndia.getTestingData()
        }

        if (testingStatus is Status.Success) {
            testDataProcessor.process(testingStatus.data)
        }
    }

    private suspend fun syncAppLaunchData(service: FirebaseHostApiService, callback: (Status<*>) -> Unit) {
        val status = ApiHelper.handleRequest(StatusId.LAUNCH_DATA) { service.launchPayload() }
        if (status is Status.Success) {
            status.data.shareUrl?.let { PreferenceHelper.putString(Constants.KEY_SHARE_URL, it) }
        }
        callback(status)
    }
}
package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CovidIndiaDataProcessor(
    private val apiProvider: APIProvider,
    private val covidDatabase: CovidDatabase
) {

    private val stateDataProcessor by lazy { DailyStateDataProcessor(covidDatabase) }
    private val districtDataProcessor by lazy { DistrictDataProcessor(covidDatabase) }
    private val overallDataProcessor by lazy { OverallDataProcessor(covidDatabase) }
    private val testDataProcessor by lazy { TestDataProcessor(covidDatabase) }

    suspend fun startSync(callback: (Status<*>) -> Unit) {

        val status = ApiHelper.handleRequest { apiProvider.covidIndia.getOverAllData() }
        when (status) {
            is Status.Success -> overallDataProcessor.process(status.data)
        }
        callback(status)

        coroutineScope {

            val districtResponse = async { apiProvider.covidIndia.getDistrictData() }
            val testDataResponse = async { apiProvider.covidIndia.getTestingData() }

            when (val status = ApiHelper.handleRequest { districtResponse.await() }) {
                is Status.Success -> districtDataProcessor.process(status.data)
            }

            when (val status = ApiHelper.handleRequest { testDataResponse.await() }) {
                is Status.Success -> testDataProcessor.process(status.data)
            }
        }
    }
}
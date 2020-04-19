package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.security.auth.callback.Callback

class CovidIndiaDataProcessor(private val apiProvider: APIProvider, private val covidDatabase: CovidDatabase) {

    private val stateDataProcessor by lazy { DailyStateDataProcessor(covidDatabase) }
    private val districtDataProcessor by lazy { DistrictDataProcessor(covidDatabase) }
    private val overallDataProcessor by lazy { OverallDataProcessor(covidDatabase) }

    suspend fun startSync(callback: (Status<*>) -> Unit) {
        withContext(Dispatchers.IO) {

            val status = ApiHelper.handleRequest { apiProvider.covidIndia.getOverAllData() }
            when(status) {
                is Status.Success -> overallDataProcessor.process(status.data)
            }
            callback(status)

            when(val status = ApiHelper.handleRequest { apiProvider.covidIndia.getDistrictData() }) {
                is Status.Success -> districtDataProcessor.process(status.data)
            }
        }
    }
}
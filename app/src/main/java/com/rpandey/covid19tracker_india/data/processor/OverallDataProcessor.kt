package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.data.model.covidIndia.DistrictData
import com.rpandey.covid19tracker_india.data.model.covidIndia.OverAllDataResponse
import com.rpandey.covid19tracker_india.database.entity.*
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.text.SimpleDateFormat
import java.util.*

class OverallDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<HashMap<String, OverAllDataResponse>>(covidDatabase) {

    private val testingDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val stateDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun process(data: HashMap<String, OverAllDataResponse>) {
        if (data.isNotEmpty()) {
            processStateData(data)
        }
    }

    private fun processStateData(data: HashMap<String, OverAllDataResponse>) {

        val confirmedCases = mutableListOf<ConfirmedEntity>()
        val recoveredCases = mutableListOf<RecoverEntity>()
        val deceasedCases = mutableListOf<DeceasedEntity>()
        val activeCases = mutableListOf<ActiveEntity>()
        val statesData = mutableListOf<StateEntity>()
        val districtData = mutableListOf<DistrictEntity>()
        val testData = mutableListOf<TestEntity>()

        data.forEach loop@{ (stateCode, stateData) ->
            val stateCode = stateCode.trim().toUpperCase(Locale.getDefault())
            val stateName = IndianStates.from(stateCode).stateName

            try {
                val stateTimestamp = getTimestamp(stateDateFormat, stateData.meta?.lastUpdated)
                val testingTimestamp = getTimestamp(testingDateFormat, stateData.meta?.tested?.lastUpdated)

                val deltaData = stateData.delta
                val totalData = stateData.total

                if (stateCode == Constants.STATE_TOTAL_CASE) {
                    testData.add(
                        TestEntity(
                            testingTimestamp,
                            Country.INDIA.code,
                            TestEntity.OVER_ALL,
                            deltaData?.tested ?: 0,
                            totalData?.tested ?: 0,
                            0,
                            totalData?.vaccinated ?: 0
                        )
                    )
                    return@loop
                }

                confirmedCases.add(
                    ConfirmedEntity(
                        stateTimestamp,
                        Country.INDIA.code,
                        stateCode,
                        deltaData?.confirmed ?: 0,
                        totalData?.confirmed ?: 0
                    )
                )

                recoveredCases.add(
                    RecoverEntity(
                        stateTimestamp,
                        Country.INDIA.code,
                        stateCode,
                        deltaData?.recovered ?: 0,
                        totalData?.recovered ?: 0
                    )
                )

                deceasedCases.add(
                    DeceasedEntity(
                        stateTimestamp,
                        Country.INDIA.code,
                        stateCode,
                        deltaData?.deceased ?: 0,
                        totalData?.deceased ?: 0
                    )
                )

                activeCases.add(
                    ActiveEntity(
                        stateTimestamp,
                        Country.INDIA.code,
                        stateCode,
                        totalData?.getActive() ?: 0,
                        deltaData?.getActive() ?: 0
                    )
                )

                statesData.add(
                    StateEntity(
                        Country.INDIA.code,
                        stateCode,
                        stateName,
                        stateData.meta?.population ?: 0
                    )
                )

                testData.add(
                    TestEntity(
                        0,
                        Country.INDIA.code,
                        stateName,
                        deltaData?.tested ?: 0,
                        totalData?.tested ?: 0,
                        0,
                        totalData?.vaccinated ?: 0
                    )
                )

                generateDistrictData(districtData, stateName, stateData.districts)

            } catch (e: Exception) {}
        }

        persistStateData(activeCases, confirmedCases, recoveredCases, deceasedCases, statesData, districtData, testData)

    }

    private fun generateDistrictData(
        districtData: MutableList<DistrictEntity>,
        stateName: String,
        districts: HashMap<String, DistrictData>?) {

        if (!districts.isNullOrEmpty()) {
            districts.forEach { (district, data) ->
                districtData.add(
                    DistrictEntity(
                        getDistrictId(stateName, district),
                        Country.INDIA.code,
                        stateName,
                        district,
                        data.delta?.confirmed ?: 0,
                        data.total?.confirmed ?: 0,
                        data.delta?.recovered ?: 0,
                        data.total?.recovered ?: 0,
                        data.delta?.deceased ?: 0,
                        data.total?.deceased ?: 0,
                        data.delta?.tested ?: 0,
                        data.total?.tested ?: 0,
                        data.meta?.population ?: 0,
                        null,
                        0,
                        data.total?.vaccinated ?: 0
                    )
                )
            }
        }
    }

    private fun getDistrictId(state: String, district: String): Int {
        return (state + district).hashCode()
    }

    private fun getTimestamp(dateFormat: SimpleDateFormat, lastUpdated: String?): Long {
        try {
            return if (lastUpdated != null) dateFormat.parse(lastUpdated.trim())?.time ?: 0 else 0
        } catch (e: Exception) {}

        return 0
    }

    @Transaction
    private fun persistStateData(
        activeCases: MutableList<ActiveEntity>,
        confirmedCases: MutableList<ConfirmedEntity>,
        recoveredCases: MutableList<RecoverEntity>,
        deceasedCases: MutableList<DeceasedEntity>,
        statesData: MutableList<StateEntity>,
        districtData: MutableList<DistrictEntity>,
        testData: MutableList<TestEntity>) {

        covidDatabase.activeDao().insert(activeCases)
        covidDatabase.confirmedDao().insert(confirmedCases)
        covidDatabase.recoveredDao().insert(recoveredCases)
        covidDatabase.deceasedDao().insert(deceasedCases)
        covidDatabase.stateDao().insert(statesData)
        covidDatabase.districtDao().insert(districtData)
        covidDatabase.testDao().insert(testData)
    }
}
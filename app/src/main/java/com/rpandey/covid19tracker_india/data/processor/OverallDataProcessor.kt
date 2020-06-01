package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.*
import com.rpandey.covid19tracker_india.database.entity.*
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.text.SimpleDateFormat
import java.util.*

class OverallDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<OverAllDataResponse>(covidDatabase) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())

    override fun process(data: OverAllDataResponse) {
        data.stateData?.let { processStateData(it) }
        data.testData?.let { processTestData(it) }
        data.dailyChanges?.let { processDailyChanges(it) }
    }

    private fun processStateData(data: List<StateData>) {

        val confirmedCases = mutableListOf<ConfirmedEntity>()
        val recoveredCases = mutableListOf<RecoverEntity>()
        val deceasedCases = mutableListOf<DeceasedEntity>()
        val activeCases = mutableListOf<ActiveEntity>()
        val statesData = mutableListOf<StateEntity>()

        data.forEach loop@{ stateData ->

            if (stateData.stateCode == Constants.STATE_TOTAL_CASE)
                return@loop

            try {
                val parsedTimestamp = dateFormat.parse(stateData.lastUpdatedTime.trim())
                val timestamp = parsedTimestamp?.time ?: 0

                confirmedCases.add(
                    ConfirmedEntity(
                        timestamp,
                        Country.INDIA.code,
                        stateData.stateCode.trim(),
                        stateData.deltaConfirmed,
                        stateData.confirmed
                    )
                )

                recoveredCases.add(
                    RecoverEntity(
                        timestamp,
                        Country.INDIA.code,
                        stateData.stateCode.trim(),
                        stateData.deltaRecovered,
                        stateData.recovered
                    )
                )

                deceasedCases.add(
                    DeceasedEntity(
                        timestamp,
                        Country.INDIA.code,
                        stateData.stateCode.trim(),
                        stateData.deltaDeaths,
                        stateData.deaths
                    )
                )

                activeCases.add(
                    ActiveEntity(
                        timestamp,
                        Country.INDIA.code,
                        stateData.stateCode.trim(),
                        stateData.active
                    )
                )

                statesData.add(
                    StateEntity(
                        Country.INDIA.code,
                        stateData.stateCode.trim(),
                        stateData.stateName.trim()
                    )
                )
            } catch (e: Exception) {}
        }

        persistStateData(activeCases, confirmedCases, recoveredCases, deceasedCases, statesData)

    }

    private fun processDailyChanges(data: List<DailyChanges>) {
        val dailyChanges = mutableListOf<DailyChangesEntity>()
        var index = 0
        data.takeLast(15).forEach { // take last 10 data
            try {
                dailyChanges.add(
                    DailyChangesEntity(
                        index++,
                        Country.INDIA.code,
                        it.deltaConfirmed.toInt(),
                        it.totalConfirmed.toInt(),
                        it.deltaDeceased.toInt(),
                        it.totalDeceased.toInt(),
                        it.deltaRecovered.toInt(),
                        it.totalRecovered.toInt(),
                        it.date.toString()
                    )
                )
            } catch (e: Exception) {}
        }

        if (dailyChanges.isNotEmpty()) {
            persistDailyChanges(dailyChanges)
        }
    }

    @Transaction
    private fun persistStateData(
        activeCases: MutableList<ActiveEntity>,
        confirmedCases: MutableList<ConfirmedEntity>,
        recoveredCases: MutableList<RecoverEntity>,
        deceasedCases: MutableList<DeceasedEntity>,
        statesData: MutableList<StateEntity>) {

        covidDatabase.activeDao().insert(activeCases)
        covidDatabase.confirmedDao().insert(confirmedCases)
        covidDatabase.recoveredDao().insert(recoveredCases)
        covidDatabase.deceasedDao().insert(deceasedCases)
        covidDatabase.stateDao().insert(statesData)
    }

    private fun processTestData(data: List<OverAllTestData>) {
        // convert to TestData processor format
        val testDataProcessor = TestDataProcessor(covidDatabase)
        val testDataList = mutableListOf<TestData>()
        data.forEach loop@{
            if (it.date.isEmpty() || it.totalTested.isEmpty())
                return@loop

            try {
                // convert to dd/mm/yyyy format
                val originalFormat = dateFormat
                val targetFormat = testDataProcessor.dateFormat
                val date = originalFormat.parse(it.date)
                if (date != null) {
                    val formattedDate = targetFormat.format(date)
                    testDataList.add(
                        TestData(
                            TestEntity.OVER_ALL,
                            it.totalTested,
                            formattedDate,
                            -1L
                        )
                    )
                }
            } catch (e: Exception) {}
        }

        testDataProcessor.process(TestResponse(testDataList))
    }

    @Transaction
    private fun persistDailyChanges(entities: MutableList<DailyChangesEntity>) {
        covidDatabase.dailyChangesDao().delete()
        covidDatabase.dailyChangesDao().insert(entities)
    }
}
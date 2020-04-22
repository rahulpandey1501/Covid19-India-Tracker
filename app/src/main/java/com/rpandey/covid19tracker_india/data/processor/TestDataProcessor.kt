package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.TestData
import com.rpandey.covid19tracker_india.data.model.covidIndia.TestResponse
import com.rpandey.covid19tracker_india.database.entity.TestEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.text.SimpleDateFormat
import java.util.*

class TestDataProcessor(database: CovidDatabase) : ResponseProcessor<TestResponse>(database) {

    private val _dateFormat = "dd/mm/yyyy"

    override fun process(data: TestResponse) {

        val testingEntities = mutableListOf<TestEntity>()
        val stateDataMapping = hashMapOf<String, MutableList<TestData>>()
        val dateFormat = SimpleDateFormat(_dateFormat, Locale.getDefault())

        data.testData.forEach loop@{ testData ->
            if (testData.totalTest.isNullOrEmpty())
                return@loop

            val timestamp = dateFormat.parse(testData.date)?.time ?: 0
            if (timestamp == 0L)
                return@loop

            testData.timestamp = timestamp
            stateDataMapping.getOrPut(testData.stateName, { mutableListOf() }).apply {
                add(testData)
            }
        }

        // get last two updated data of every states
        stateDataMapping.forEach { (stateName, data) ->
            data.sortByDescending { it.timestamp }
            val currentUpdateData = data.first()
            val lastUpdateData = data.getOrNull(1)

            val currentTotalCount = currentUpdateData.totalTest!!.toInt()
            val lastTotalCount = lastUpdateData?.totalTest?.toInt() ?: 0
            val deltaCount = if (lastTotalCount == 0) 0 else currentTotalCount - lastTotalCount

            testingEntities.add(
                TestEntity(
                    currentUpdateData.timestamp,
                    Country.INDIA.code,
                    stateName,
                    deltaCount,
                    currentTotalCount
                )
            )
        }

        if (testingEntities.isNotEmpty()) {
            persistData(testingEntities)
        }
    }

    private fun persistData(entities: List<TestEntity>) {
        covidDatabase.testDao().insert(entities)
    }
}
package com.rpandey.covid19tracker_india.data.processor

import androidx.annotation.StringDef
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.data.model.covidIndia.DailyStateResponse
import com.rpandey.covid19tracker_india.database.entity.ActiveEntity
import com.rpandey.covid19tracker_india.database.entity.ConfirmedEntity
import com.rpandey.covid19tracker_india.database.entity.DeceasedEntity
import com.rpandey.covid19tracker_india.database.entity.RecoverEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.util.*

@Deprecated("USE OVERALL DATA")
class DailyStateDataProcessor(covidDatabase: CovidDatabase): ResponseProcessor<DailyStateResponse>(covidDatabase) {

    @StringDef(
        Constants.CONFIRMED,
        Constants.DECEASED,
        Constants.RECOVERED,
        Constants.DATE,
        Constants.STATUS,
        Constants.TOTAL_CASE
    )
    annotation class Constants {
        companion object {
            const val STATUS = "status"
            const val CONFIRMED = "Confirmed"
            const val RECOVERED = "Recovered"
            const val DECEASED = "Deceased"
            const val DATE = "date"
            const val TOTAL_CASE = "TT"
        }
    }

    override fun process(data: DailyStateResponse) {
        data.stateDaily?.let { processStateData(it)  }
    }

    private fun processStateData(response: List<MutableMap<String, String>>) {

        val confirmedCases = mutableListOf<ConfirmedEntity>()
        val recoveredCases = mutableListOf<RecoverEntity>()
        val deceasedCases = mutableListOf<DeceasedEntity>()
        val activeCases = mutableListOf<ActiveEntity>()

        val indianStates = IndianStates.values().associateBy { it.stateCode }

        response.forEach {
            val date = 0L

            if (it[Constants.STATUS] == Constants.CONFIRMED) {
                it.forEach { (_state, value) ->
                    val state = _state.toUpperCase(Locale.getDefault())
                    if (indianStates.containsKey(state)) {
                        confirmedCases.add(
                            ConfirmedEntity(
                                date,
                                Country.INDIA.code,
                                state,
                                value.toIntOrNull() ?: 0,
                                -1
                            )
                        )
                    }
                }
            }

            if (it[Constants.STATUS] == Constants.RECOVERED) {
                it.forEach { (_state, value) ->
                    val state = _state.toUpperCase(Locale.getDefault())
                    if (indianStates.containsKey(state)) {
                        recoveredCases.add(
                            RecoverEntity(
                                date,
                                Country.INDIA.code,
                                state,
                                value.toIntOrNull() ?: 0,
                                -1
                            )
                        )
                    }
                }
            }

            if (it[Constants.STATUS] == Constants.DECEASED) {
                it.forEach { (_state, value) ->
                    val state = _state.toUpperCase(Locale.getDefault())
                    if (indianStates.containsKey(state)) {
                        deceasedCases.add(
                            DeceasedEntity(
                                date,
                                Country.INDIA.code,
                                state,
                                value.toIntOrNull() ?: 0,
                                -1
                            )
                        )
                    }
                }
            }
        }

        processConfirmedTotalCases(confirmedCases)
        processRecoveredTotalCases(recoveredCases)
        processDeceasedTotalCases(deceasedCases)
        processActiveCases(activeCases, confirmedCases, recoveredCases, deceasedCases)

        persistData(activeCases, confirmedCases, recoveredCases, deceasedCases)
    }

    private fun processConfirmedTotalCases(cases: MutableList<ConfirmedEntity>) {
        cases.sortBy { it.date }
        var totalCount = 0
        cases.forEach {
            totalCount += it.confirmed
            it.totalConfirmed = totalCount
        }
    }

    private fun processRecoveredTotalCases(cases: MutableList<RecoverEntity>) {
        cases.sortBy { it.date }
        var totalCount = 0
        cases.forEach {
            totalCount += it.recovered
            it.totalRecovered = totalCount
        }
    }

    private fun processDeceasedTotalCases(cases: MutableList<DeceasedEntity>) {
        cases.sortBy { it.date }
        var totalCount = 0
        cases.forEach {
            totalCount += it.deceased
            it.totalDeceased = totalCount
        }
    }

    private fun processActiveCases(
        activeCases: MutableList<ActiveEntity>,
        confirmedCases: MutableList<ConfirmedEntity>,
        recoveredCases: MutableList<RecoverEntity>,
        deceasedCases: MutableList<DeceasedEntity>
    ) {

        var totalActiveCount = 0

        repeat(confirmedCases.size) { index ->
            val confirmedCase = confirmedCases[index]
            val recoveredCase = recoveredCases[index]
            val deceasedCase = deceasedCases[index]

            val date = confirmedCase.date
            val country = confirmedCase.country
            val state = confirmedCase.state

            val confirmedCount = confirmedCase.totalConfirmed
            val recoveredCount = recoveredCase.totalRecovered
            val deceasedCount = deceasedCase.totalDeceased

            val currentCount = confirmedCount.minus(recoveredCount).minus(deceasedCount)
            totalActiveCount += currentCount

            activeCases.add(
                ActiveEntity(date, country, state, currentCount)
            )
        }
    }

    private fun persistData(
        activeCases: MutableList<ActiveEntity>,
        confirmedCases: MutableList<ConfirmedEntity>,
        recoveredCases: MutableList<RecoverEntity>,
        deceasedCases: MutableList<DeceasedEntity>) {

        covidDatabase.activeDao().insert(activeCases)
        covidDatabase.confirmedDao().insert(confirmedCases)
        covidDatabase.recoveredDao().insert(recoveredCases)
        covidDatabase.deceasedDao().insert(deceasedCases)
    }

}
package com.rpandey.covid19tracker_india.data.repository

import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider

class CovidIndiaRepository(private val covidDatabase: CovidDatabase) {

    fun getConfirmedCount() = covidDatabase.confirmedDao().getCurrentCount(Country.INDIA.code)

    fun getActiveCount() = covidDatabase.activeDao().getCurrentCount(Country.INDIA.code)

    fun getRecoveredCount() = covidDatabase.recoveredDao().getCurrentCount(Country.INDIA.code)

    fun getDeceasedCount() = covidDatabase.deceasedDao().getCurrentCount(Country.INDIA.code)

}
package com.rpandey.covid19tracker_india.data.repository

import androidx.lifecycle.LiveData
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.BookmarkType
import com.rpandey.covid19tracker_india.database.entity.BookmarkedEntity
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CovidIndiaRepository(private val covidDatabase: CovidDatabase) {

    fun getConfirmedCount() = covidDatabase.confirmedDao().getCurrentCount(Country.INDIA.code)

    fun getActiveCount() = covidDatabase.activeDao().getCurrentCount(Country.INDIA.code)

    fun getRecoveredCount() = covidDatabase.recoveredDao().getCurrentCount(Country.INDIA.code)

    fun getDeceasedCount() = covidDatabase.deceasedDao().getCurrentCount(Country.INDIA.code)

    fun searchDistrict(district: String?) =
        covidDatabase.districtDao().getByDistrictName("%${district}%")

    fun getStates() = covidDatabase.stateDao().getStates(Country.INDIA.code)

    fun setBookmark(bookmarkId: String, @BookmarkType type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            covidDatabase.bookmarkDao()
                .addBookmark(BookmarkedEntity(bookmarkId, type))
        }
    }

    fun removeBookmark(bookmarkId: String, @BookmarkType type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            covidDatabase.bookmarkDao()
                .deleteBookmark(BookmarkedEntity(bookmarkId, type))
        }
    }

    fun getBookmarkedDistricts(): LiveData<List<DistrictEntity>> {
        return covidDatabase.bookmarkDao().getDistricts()
    }

    fun getBookmarkedCombinedCases(): LiveData<List<CombinedCasesModel>> {
        return covidDatabase.bookmarkDao().getCombinedCases()
    }

    fun getCombinedCases(): LiveData<List<CombinedCasesModel>> {
        return covidDatabase.combinedCasesDao().getOverall()
    }

    fun lastUpdatedTime(): LiveData<Long?> {
        return covidDatabase.confirmedDao().lastUpdatedTime()
    }

}
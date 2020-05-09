package com.rpandey.covid19tracker_india.data.repository

import androidx.lifecycle.LiveData
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.BookmarkType
import com.rpandey.covid19tracker_india.database.entity.BookmarkedEntity
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.entity.TestEntity
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CovidIndiaRepository(private val covidDatabase: CovidDatabase) {

    fun getConfirmedCount(state: String? = null): LiveData<CountModel> {
        return if (state != null) {
            covidDatabase.confirmedDao().getCurrentCount(Country.INDIA.code, state)
        } else {
            covidDatabase.confirmedDao().getCurrentCount(Country.INDIA.code)
        }
    }

    fun getActiveCount(state: String? = null): LiveData<Int> {
        return if (state != null) {
            covidDatabase.activeDao().getCurrentCount(Country.INDIA.code, state)
        } else {
            covidDatabase.activeDao().getCurrentCount(Country.INDIA.code)
        }
    }

    fun getRecoveredCount(state: String? = null): LiveData<CountModel> {
        return if (state != null) {
            covidDatabase.recoveredDao().getCurrentCount(Country.INDIA.code, state)
        } else {
            covidDatabase.recoveredDao().getCurrentCount(Country.INDIA.code)
        }
    }

    fun getDeceasedCount(state: String? = null): LiveData<CountModel> {
        return if (state != null) {
            covidDatabase.deceasedDao().getCurrentCount(Country.INDIA.code, state)
        } else {
            covidDatabase.deceasedDao().getCurrentCount(Country.INDIA.code)
        }
    }

    fun getTestingCount(state: String = TestEntity.OVER_ALL) = covidDatabase.testDao().getTotalCount(state)

    fun searchDistrict(district: String?) = covidDatabase.districtDao().getByDistrictName("%${district}%")

    fun getDistricts(stateName: String, count: Int = -1): LiveData<List<DistrictEntity>> {
        return if (count > 0) {
            covidDatabase.districtDao().getByState(Country.INDIA.code, stateName, count)
        } else {
            covidDatabase.districtDao().getByState(Country.INDIA.code, stateName)
        }
    }

    fun getDistrict(districtId: Int) = covidDatabase.districtDao().getByDistrictId(districtId)

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

    fun getCombinedNewCases(): LiveData<List<CombinedCasesModel>> {
        return covidDatabase.combinedCasesDao().getOverallNewCases()
    }

    fun lastUpdatedTime(state: String? = null): LiveData<Long?> {
        return if (state != null) {
            covidDatabase.confirmedDao().lastUpdatedTime(state)
        } else  {
            covidDatabase.confirmedDao().lastUpdatedTime()
        }
    }

    fun lastUpdatedTime(districtId: Int): LiveData<Long?> {
        return covidDatabase.districtDao().lastUpdatedTime(districtId)
    }

    fun searchState(keyword: String): List<CombinedCasesModel> {
        return covidDatabase.combinedCasesDao().searchOverall("%${keyword}%")
    }
}
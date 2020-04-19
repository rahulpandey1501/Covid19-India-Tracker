package com.rpandey.covid19tracker_india.ui.dashboard

import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.BookmarkType
import com.rpandey.covid19tracker_india.database.entity.StateEntity

class DashboardViewModel(private val repository: CovidIndiaRepository) : ViewModel() {


    fun onSateSelected(stateEntity: StateEntity) {
        repository.setBookmark(stateEntity.code, BookmarkType.STATE)
    }

    fun onCombinedCasesBookmarkRemoved(combinedCasesModel: CombinedCasesModel) {
        repository.removeBookmark(combinedCasesModel.state, BookmarkType.STATE)
    }

    fun onDistrictSelected(districtId: Int) {
        repository.setBookmark(districtId.toString(), BookmarkType.DISTRICT)
    }

    fun onDistrictRemoved(districtId: Int) {
        repository.removeBookmark(districtId.toString(), BookmarkType.DISTRICT)
    }

    fun getBookmarkedDistricts() = repository.getBookmarkedDistricts()

    fun getBookmarkedCombinedCases() = repository.getBookmarkedCombinedCases()
}
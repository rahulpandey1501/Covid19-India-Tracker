package com.rpandey.covid19tracker_india.ui.topcases

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity

class TopCasesViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getDistricts(): LiveData<List<DistrictEntity>> {
        return Transformations.map(repository.getDistricts(null, 18)) { data ->
            data.filter { !getBlacklistedDistricts().contains(it.district.toLowerCase()) }.take(12)
        }
    }

    private fun getBlacklistedDistricts(): Array<String> {
        return arrayOf("unknown", "unassigned")
    }
}
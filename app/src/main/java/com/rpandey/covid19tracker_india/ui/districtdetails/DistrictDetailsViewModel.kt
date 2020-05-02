package com.rpandey.covid19tracker_india.ui.districtdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.util.Util

class DistrictDetailsViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getDistrict(districtId: Int) = repository.getDistrict(districtId)

    fun lastUpdatedTime(districtId: Int): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime(districtId)) {
            if (it != null) Util.timestampToDate(it) else "NA"
        }
    }
}
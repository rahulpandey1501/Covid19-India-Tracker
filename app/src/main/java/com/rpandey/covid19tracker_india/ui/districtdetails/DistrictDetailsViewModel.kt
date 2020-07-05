package com.rpandey.covid19tracker_india.ui.districtdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DistrictDetailsViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    data class MetaInfo(
        val stateName: String,
        val totalCases: Int,
        val totalCasesByState: Int,
        val positionByState: Int,
        val positionByOverall: Int,
        val totalTesting: Int
    )

    val getMetaInfo = MediatorLiveData<MetaInfo>()
    val getDistrictInfo = MediatorLiveData<DistrictEntity>()

    fun init(districtId: Int) {
        generateDistrictData(districtId)
    }

    private fun generateDistrictData(districtId: Int) {
        getMetaInfo.addSource(repository.getDistricts(null)) { allDistricts ->
            CoroutineScope(Dispatchers.IO).launch {
                allDistricts.find { it.districtId == districtId }?.let { district ->
                    getDistrictInfo.postValue(district)
                    val totalCases = district.totalConfirmed
                    val positionByOverall = allDistricts.indexOf(district) + 1
                    val districtsFilterByState = allDistricts.filter { it.stateName == district.stateName }
                    val totalCasesByState = districtsFilterByState.sumBy { it.totalConfirmed }
                    val positionByState = districtsFilterByState.indexOf(district) + 1
                    getMetaInfo.postValue(
                        MetaInfo(
                            district.stateName,
                            totalCases,
                            totalCasesByState,
                            positionByState,
                            positionByOverall,
                            district.totalTested
                        )
                    )
                }
            }
        }
    }

    fun lastUpdatedTime(districtId: Int): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime(districtId)) {
            if (it != null) Util.timestampToDate(it) else "NA"
        }
    }

    fun hasResources(stateName: String, district: String): LiveData<List<String>> {
        return repository.getResourceCategories(stateName, district)
    }
}
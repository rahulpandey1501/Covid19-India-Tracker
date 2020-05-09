package com.rpandey.covid19tracker_india.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import kotlinx.coroutines.*

class SearchDistrictViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private var checkVpaJob: Job? = null
    private val TYPE_THRESHOLD = 400L

    private val _districtSearch = MediatorLiveData<List<DistrictEntity>>()
    val districtSearch: LiveData<List<DistrictEntity>> = _districtSearch

    private val _overAllSearch = MediatorLiveData<Pair<List<DistrictEntity>, List<CombinedCasesModel>>>()
    val overAllSearch: LiveData<Pair<List<DistrictEntity>, List<CombinedCasesModel>>> = _overAllSearch

    fun getDistrict(districtSearch: String?) {
        cancelExistingJob()

        if (districtSearch.isNullOrEmpty()) {
            _districtSearch.postValue(emptyList())
            return
        }

        checkVpaJob = CoroutineScope(Dispatchers.IO).launch {
            delay(TYPE_THRESHOLD) // make a delay of 500ms
            _districtSearch.postValue(repository.searchDistrict(districtSearch))
        }
    }

    fun getOverAllSearch(keyword: String?) {
        cancelExistingJob()

        if (keyword.isNullOrEmpty()) {
            _overAllSearch.postValue(emptyList<DistrictEntity>() to emptyList<CombinedCasesModel>())
            return
        }

        checkVpaJob = CoroutineScope(Dispatchers.IO).launch {
            delay(TYPE_THRESHOLD) // make a delay of 500ms
            val districtResult = repository.searchDistrict(keyword)
            val stateResult = repository.searchState(keyword)
            _overAllSearch.postValue(districtResult to stateResult)
        }
    }

    private fun cancelExistingJob() {
        checkVpaJob?.cancel()
    }
}
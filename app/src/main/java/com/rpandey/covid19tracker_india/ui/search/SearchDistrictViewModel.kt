package com.rpandey.covid19tracker_india.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import kotlinx.coroutines.*

class SearchDistrictViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private var checkVpaJob: Job? = null
    private val TYPE_THRESHOLD = 300L

    private val _districtSearch = MediatorLiveData<List<DistrictEntity>>()
    val districtSearch: LiveData<List<DistrictEntity>> = _districtSearch

    fun getDistrict(districtSearch: String?) {
        cancelExistingJob()

        if (districtSearch.isNullOrEmpty()) {
            _districtSearch.postValue(emptyList())
            return
        }

        checkVpaJob = CoroutineScope(Dispatchers.IO).launch {
            delay(TYPE_THRESHOLD) // make a delay of 500ms
            withContext(Dispatchers.Main) {
                _districtSearch.addSource(repository.searchDistrict(districtSearch)) {
                    _districtSearch.postValue(it)
                }
            }
        }
    }

    private fun cancelExistingJob() {
        checkVpaJob?.cancel()
    }
}
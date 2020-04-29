package com.rpandey.covid19tracker_india.ui.home

import androidx.lifecycle.*
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.util.Util
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getConfirmedCount() = repository.getConfirmedCount()

    fun getActiveCount() = repository.getActiveCount()

    fun getRecoveredCount() = repository.getRecoveredCount()

    fun getDeceasedCount() = repository.getDeceasedCount()

    fun getTestingCount() = repository.getTestingCount()

    fun lastUpdatedTime(): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime()) {
            if (it != null)
                Util.timestampToDate(it)
            else ""
        }
    }
}
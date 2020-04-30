package com.rpandey.covid19tracker_india.ui.home

import androidx.lifecycle.*
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.util.Util

class HomeViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private fun getConfirmedCount() = repository.getConfirmedCount()

    private fun getActiveCount() = repository.getActiveCount()

    private fun getRecoveredCount() = repository.getRecoveredCount()

    private fun getDeceasedCount() = repository.getDeceasedCount()

    private fun getTestingCount() = repository.getTestingCount()

    fun getCount(): MediatorLiveData<Map<UICaseType, CountModel>> {
        val mediatorLiveData = MediatorLiveData<Map<UICaseType, CountModel>>()

        val allCases = mutableMapOf<UICaseType, CountModel>()

        mediatorLiveData.addSource(getConfirmedCount(), Observer {
            it?.let {
                allCases[UICaseType.TYPE_CONFIRMED] = it
                mediatorLiveData.value = allCases
            }
        })

        mediatorLiveData.addSource(getActiveCount(), Observer {
            it?.let {
                allCases[UICaseType.TYPE_ACTIVE] = CountModel(0, it)
                mediatorLiveData.value = allCases
            }
        })

        mediatorLiveData.addSource(getRecoveredCount(), Observer {
            it?.let {
                allCases[UICaseType.TYPE_RECOVERED] = it
                mediatorLiveData.value = allCases
            }
        })

        mediatorLiveData.addSource(getDeceasedCount(), Observer {
            it?.let {
                allCases[UICaseType.TYPE_DEATH] = it
                mediatorLiveData.value = allCases
            }
        })

        mediatorLiveData.addSource(getTestingCount(), Observer {
            it?.let {
                allCases[UICaseType.TYPE_TESTING] = it
                mediatorLiveData.value = allCases
            }
        })

        return mediatorLiveData
    }

    fun lastUpdatedTime(): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime()) {
            if (it != null)
                Util.timestampToDate(it)
            else "NA"
        }
    }
}
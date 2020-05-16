package com.rpandey.covid19tracker_india.ui.statedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.ui.home.UICaseType
import com.rpandey.covid19tracker_india.util.Util

class StateDetailsViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private fun getConfirmedCount(state: String) = repository.getConfirmedCount(state)

    private fun getActiveCount(state: String) = repository.getActiveCount(state)

    private fun getRecoveredCount(state: String) = repository.getRecoveredCount(state)

    private fun getDeceasedCount(state: String) = repository.getDeceasedCount(state)

    private fun getTestingCount(state: String) = repository.getTestingCount(state)

    fun getDistricts(stateName: String) = repository.getDistricts(stateName)

    fun getCount(state: String, stateName: String): MediatorLiveData<Map<UICaseType, CountModel>> {
        val allCases = mutableMapOf<UICaseType, CountModel>()

        return MediatorLiveData<Map<UICaseType, CountModel>>().apply {
            addSource(getConfirmedCount(state), Observer {
                it?.let {
                    allCases[UICaseType.TYPE_CONFIRMED] = it
                    value = allCases
                }
            })

            addSource(getActiveCount(state), Observer {
                it?.let {
                    allCases[UICaseType.TYPE_ACTIVE] = CountModel(0, it)
                    value = allCases
                }
            })

            addSource(getRecoveredCount(state), Observer {
                it?.let {
                    allCases[UICaseType.TYPE_RECOVERED] = it
                    value = allCases
                }
            })

            addSource(getDeceasedCount(state), Observer {
                it?.let {
                    allCases[UICaseType.TYPE_DEATH] = it
                    value = allCases
                }
            })

            addSource(getTestingCount(stateName), Observer {
                it?.let {
                    allCases[UICaseType.TYPE_TESTING] = it
                    value = allCases
                }
            })
        }
    }

    fun lastUpdatedTime(state: String): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime(state)) {
            if (it != null) Util.timestampToDate(it) else "NA"
        }
    }
}
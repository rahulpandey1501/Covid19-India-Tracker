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

    private fun getVaccinationCount() = repository.getVaccinationCount()

    fun getCount(): MediatorLiveData<Map<UICaseType, CountModel>> {
        val allCases = mutableMapOf<UICaseType, CountModel>()

        return MediatorLiveData<Map<UICaseType, CountModel>>().apply {
            addSource(getConfirmedCount()) {
                it?.let {
                    allCases[UICaseType.TYPE_CONFIRMED] = it
                    value = allCases
                }
            }

            addSource(getActiveCount()) {
                allCases[UICaseType.TYPE_ACTIVE] = it
                value = allCases
            }

            addSource(getRecoveredCount()) {
                it?.let {
                    allCases[UICaseType.TYPE_RECOVERED] = it
                    value = allCases
                }
            }

            addSource(getDeceasedCount()) {
                it?.let {
                    allCases[UICaseType.TYPE_DEATH] = it
                    value = allCases
                }
            }

            addSource(getTestingCount()) {
                it?.let {
                    allCases[UICaseType.TYPE_TESTING] = it
                    value = allCases
                }
            }

            addSource(getVaccinationCount()) {
                it?.let {
                    allCases[UICaseType.TYPE_VACCINATION] = it
                    value = allCases
                }
            }
        }
    }

    fun lastUpdatedTime(): LiveData<String> {
        return Transformations.map(repository.lastUpdatedTime()) {
            if (it != null)
                Util.timestampToDate(it)
            else "NA"
        }
    }
}
package com.rpandey.covid19tracker_india.ui.notifications

import androidx.lifecycle.*
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel

class DailyUpdatesViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getDailyUpdates(): LiveData<List<CombinedCasesModel>> {
        return Transformations.map(repository.getCombinedNewCases()) { data ->
            data.filter { it.confirmedCases > 0 || it.recoveredCases > 0 || it.deceasedCases > 0 }
        }
    }
}
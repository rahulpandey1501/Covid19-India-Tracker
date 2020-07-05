package com.rpandey.covid19tracker_india.ui.notifications.dailyupdates

import androidx.lifecycle.*
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity

class DailyUpdatesViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getDailyStateUpdates(): LiveData<List<CombinedCasesModel>> {
        return Transformations.map(repository.getCombinedNewCases()) { data ->
            data.filter {
                (it.confirmedCases > 0 || it.recoveredCases > 0 || it.deceasedCases > 0) && it.state != IndianStates.UN.stateCode
            }
        }
    }

    fun getDailyDistrictUpdates(): LiveData<List<DistrictEntity>> {
        return repository.getDistrictsNewCases()
    }
}
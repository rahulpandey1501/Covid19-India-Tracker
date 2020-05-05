package com.rpandey.covid19tracker_india.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.dao.CombinedCasesModel

class StateListViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getStatesData(): LiveData<List<CombinedCasesModel>> {
        return repository.getCombinedCases()
    }

}
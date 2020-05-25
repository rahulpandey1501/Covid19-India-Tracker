package com.rpandey.covid19tracker_india.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.StateEntity
import com.rpandey.covid19tracker_india.ui.common.ItemSelectorBottomSheet

class SelectStateViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getStates(): LiveData<List<ItemSelectorBottomSheet.Item<StateEntity>>> {
        return Transformations.map(repository.getStates()) { states ->
            states.map { ItemSelectorBottomSheet.Item(it.name, it) }
        }
    }

}
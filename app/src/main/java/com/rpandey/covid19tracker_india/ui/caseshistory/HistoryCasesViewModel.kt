package com.rpandey.covid19tracker_india.ui.caseshistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.DailyChangesEntity

class HistoryCasesViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private val count = 15

    fun getHistory(stateCode: String): LiveData<List<DataItem>> {
        return Transformations.map(repository.getDailyChanges(stateCode, count + 1)) { history ->

            val dataItems = mutableListOf<DataItem>()

            var lastConfirm: Int? = null
            var lastRecover: Int? = null
            var lastDeath: Int? = null

            history.reversed().forEach { // start from start
                if (it.recovered == 0 && it.confirmed == 0 && it.deceased == 0)
                    return@forEach

                dataItems.add(
                    DataItem(
                        it,
                        it.confirmed - (lastConfirm ?: it.confirmed),
                        it.recovered - (lastRecover ?: it.recovered),
                        it.deceased - (lastDeath ?: it.deceased)
                    )
                )

                lastConfirm = it.confirmed
                lastRecover = it.recovered
                lastDeath = it.deceased
            }

            dataItems.reversed().take(count)
        }
    }

    inner class DataItem(
        val entity: DailyChangesEntity,
        val deltaConfirm: Int,
        val deltaRecover: Int,
        val deltaDeath: Int
    )
}
package com.rpandey.covid19tracker_india.ui.topcases

import android.annotation.SuppressLint
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.model.IndianStates
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository

class TopCasesViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    annotation class Type {
        companion object {
            const val DISTRICT = 0
            const val STATE = 1
        }
    }

    @Volatile
    private var fetchCount: Int = 2 // states and district
    private val totalItemCount = 12
    val topCases = MediatorLiveData<List<DataItem>>()
    private val dataList = mutableSetOf<DataItem>()

    init {
        fetchTopCases()
    }

    @SuppressLint("DefaultLocale")
    private fun fetchTopCases() {

        // fetch districts except Delhi
        topCases.addSource(repository.getDistricts(null, totalItemCount + 3)) { data ->
            --fetchCount
            dataList.addAll(
                data.filter {
                    !getBlacklistedDistricts().contains(it.district.toLowerCase()) && !it.district.toLowerCase()
                        .split(" ").contains("delhi")
                }.map { DataItem(Type.DISTRICT, it.totalConfirmed, it.district, it.districtId) }
            )
            if (fetchCount <= 0)
                checkList(dataList)
        }

        topCases.addSource(repository.getConfirmedCount(IndianStates.DL.stateCode)) {
            --fetchCount
            dataList.add(DataItem(Type.STATE, it.totalCount, "Delhi", IndianStates.DL.stateCode))
            if (fetchCount <= 0)
                checkList(dataList)
        }
    }

    private fun checkList(dataList: MutableSet<DataItem>) {
        val items = dataList.sortedByDescending { it.totalConfirmed }.take(totalItemCount)
        topCases.postValue(items)
    }

    private fun getBlacklistedDistricts(): Array<String> {
        return arrayOf("unknown", "unassigned")
    }

    data class DataItem(
        val type: Int,
        val totalConfirmed: Int,
        val name: String,
        val typeIdentifier: Any
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true

            if (other?.javaClass != javaClass) return false

            other as DataItem

            return other.typeIdentifier == typeIdentifier && other.type == type
        }

        override fun hashCode(): Int {
            return 31*typeIdentifier.hashCode() + type.hashCode()
        }
    }
}
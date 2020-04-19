package com.rpandey.covid19tracker_india.ui.dashboard

import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository

class SelectStateViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getStates() = repository.getStates()

}
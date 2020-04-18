package com.rpandey.covid19tracker_india.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


//    fun getUsers() = liveData(Dispatchers.IO) {
//        emit(Status.Fetching(data = null))
//        try {
//            emit(Status.Success(data = repository.getUsers()))
//        } catch (exception: Exception) {
//            emit(Status.Error(data = null, errorMessage = exception.message ?: "Oops! Something went wrong"))
//        }
//    }

    fun getConfirmedCount() = repository.getConfirmedCount()

    fun getActiveCount() = repository.getActiveCount()

    fun getRecoveredCount() = repository.getRecoveredCount()

    fun getDeceasedCount() = repository.getDeceasedCount()
}
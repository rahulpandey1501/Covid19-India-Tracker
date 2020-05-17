package com.rpandey.covid19tracker_india.ui.essentials

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.ResourcesEntity

class EssentialsViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    val stateLD = MediatorLiveData<List<String>>()
    val districtLD = MediatorLiveData<List<String>>()
    val categoriesLD = MediatorLiveData<List<String>>()
    val resourcesLD = MediatorLiveData<List<ResourcesEntity>>()

    init {
        stateLD.addSource(repository.getResourceStates()) {
            stateLD.postValue(it)
        }
    }

    fun onStateSelected(stateName: String) {
        districtLD.addSource(repository.getResourceDistrict(stateName)) {
            districtLD.postValue(it)
        }
    }

    fun onDistrictSelected(stateName: String, district: String) {
        categoriesLD.addSource(repository.getResourceCategories(stateName, district)) {
            categoriesLD.postValue(it)
        }
    }

    fun onCategorySelected(stateName: String, district: String, category: String) {
        resourcesLD.addSource(repository.getResources(stateName, district, category)) {
            resourcesLD.postValue(it)
        }
    }
}
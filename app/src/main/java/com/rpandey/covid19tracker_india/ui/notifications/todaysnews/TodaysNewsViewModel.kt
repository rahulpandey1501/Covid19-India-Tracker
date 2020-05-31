package com.rpandey.covid19tracker_india.ui.notifications.todaysnews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.processor.CovidIndiaSyncManager
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.entity.NewsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodaysNewsViewModel(private val repository: CovidIndiaRepository) : ViewModel() {

    fun getNews(): LiveData<List<NewsEntity>> {
        return repository.getNews()
    }

    fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            CovidIndiaSyncManager.getInstance().syncNews(Country.INDIA)
        }
    }
}
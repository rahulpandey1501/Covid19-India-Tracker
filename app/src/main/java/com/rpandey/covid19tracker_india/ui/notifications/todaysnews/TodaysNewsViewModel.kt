package com.rpandey.covid19tracker_india.ui.notifications.todaysnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.model.covidIndia.news.NewsResponse
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodaysNewsViewModel(private val apiProvider: APIProvider) : ViewModel() {

    val sourceNews = MutableLiveData<String>()
    val items = MutableLiveData<List<DataItem>>()

    init {
        fetchData()
    }

    fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            val apiStatus = ApiHelper.handleRequest(StatusId.TODAYS_NEWS) {
                apiProvider.covidIndia.getNews(Constants.NEWS_URL)
            }

            if (apiStatus is Status.Success) {
                apiStatus.data.source?.let { sourceNews.postValue(it) }
                items.postValue(convertToDataItem(apiStatus.data))

            } else {
                items.postValue(emptyList())
            }
        }
    }

    private fun convertToDataItem(newsResponse: NewsResponse): List<DataItem> {
        val items = mutableListOf<DataItem>()
        val headlines = newsResponse.headlines
        val images = newsResponse.images
        val summaries = newsResponse.summary

        for (index in newsResponse.headlines.indices) {
            val headline = headlines[index]
            val summary = summaries?.get(index)
            val image = images?.get(index)

            if (summary != null && image != null) {
                items.add(
                    DataItem(headline, summary, image)
                )
            }

        }

        return items
    }

    inner class DataItem(
        val headline: String,
        val summary: String,
        val imageLink: String
    )
}
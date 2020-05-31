package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.news.NewsResponse
import com.rpandey.covid19tracker_india.database.entity.NewsEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.lang.Exception

class NewsDataProcessor(covidDatabase: CovidDatabase) : ResponseProcessor<NewsResponse>(covidDatabase) {

    override fun process(data: NewsResponse) {
        val items = mutableListOf<NewsEntity>()
        val headlines = data.headlines
        val images = data.images
        val summaries = data.summary

        for (index in data.headlines.indices) {
            try {
                val headline = headlines[index]
                val summary = summaries?.get(index)
                val image = images?.get(index)

                if (summary != null && image != null) {
                    items.add(
                        NewsEntity(Country.INDIA.code, data.source, headline, summary, image, null)
                    )
                }
            } catch (e: Exception) {}
        }

        if (items.isNotEmpty()) {
            persists(items)
        }
    }

    @Transaction
    private fun persists(data: List<NewsEntity>) {
        covidDatabase.newsDao().delete()
        covidDatabase.newsDao().insert(data)
    }
}
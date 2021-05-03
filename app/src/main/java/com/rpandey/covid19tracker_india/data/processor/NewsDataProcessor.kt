package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.news.NewsListResponse
import com.rpandey.covid19tracker_india.database.entity.NewsEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.lang.Exception

class NewsDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<NewsListResponse>(covidDatabase) {

    override fun process(data: NewsListResponse) {
        try {
            if (data.success) {
                val items = mutableListOf<NewsEntity>()
                data.data?.forEach { news ->
                    val headline = news.title
                    val summary = news.content
                    val image = news.imageUrl
                    val link = news.readMoreUrl

                    if (headline != null && summary != null && image != null) {
                        items.add(
                            NewsEntity(
                                Country.INDIA.code,
                                "inShorts",
                                headline,
                                summary,
                                image,
                                link
                            )
                        )
                    }
                }
                if (items.isNotEmpty()) {
                    persists(items)
                }
            }
        } catch (e: Exception) { }
    }

    @Transaction
    private fun persists(data: List<NewsEntity>) {
        covidDatabase.newsDao().delete()
        covidDatabase.newsDao().insert(data)
    }
}
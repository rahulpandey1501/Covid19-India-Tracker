package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.TimeSeriesResponse
import com.rpandey.covid19tracker_india.database.entity.DailyChangesEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import java.text.SimpleDateFormat
import java.util.*

class TimeSeriesDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<HashMap<String, LinkedHashMap<String, TimeSeriesResponse>>>(covidDatabase) {

    private val targetFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun process(data: HashMap<String, LinkedHashMap<String, TimeSeriesResponse>>) {
        val dailyChanges = mutableListOf<DailyChangesEntity>()
        val totalCasesSeries = data[Constants.STATE_TOTAL_CASE]
        var index = 0
        totalCasesSeries?.forEach { (seriesDate, caseData) ->
            try {
                val date = targetFormat.format(dateFormat.parse(seriesDate))
                dailyChanges.add(
                    DailyChangesEntity(
                        index++,
                        Country.INDIA.code,
                        caseData.delta?.confirmed ?: 0,
                        caseData.total?.confirmed ?: 0,
                        caseData.delta?.deceased ?: 0,
                        caseData.total?.deceased ?: 0,
                        caseData.delta?.recovered ?: 0,
                        caseData.total?.recovered ?: 0,
                        date
                    )
                )
            } catch (e: Exception) {}

        }

        if (dailyChanges.isNotEmpty()) {
            persistDailyChanges(dailyChanges)
        }
    }

    @Transaction
    private fun persistDailyChanges(entities: MutableList<DailyChangesEntity>) {
        covidDatabase.dailyChangesDao().delete()
        covidDatabase.dailyChangesDao().insert(entities)
    }
}
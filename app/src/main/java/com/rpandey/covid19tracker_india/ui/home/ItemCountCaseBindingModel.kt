package com.rpandey.covid19tracker_india.ui.home

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.util.Util

class ItemCountCaseBindingModel(private val context: Context) {

    val title = ObservableField<String>()
    val totalCount = ObservableField<String>()
    val currentCount = ObservableField<String>()
    val percentageCount = ObservableField<String>()
    val backgroundColor = ObservableInt()

    fun init(caseType: UICaseType, caseMapping: Map<UICaseType, CountModel>) {
        val countModel = caseMapping[caseType] ?: error("$caseType not found")
        val confirmCaseModel = caseMapping[UICaseType.TYPE_CONFIRMED]
        totalCount.set(Util.formatNumber(countModel.totalCount))
        if (countModel.currentCount > 0)
            currentCount.set(Util.formatNumber(countModel.currentCount))

        when(caseType) {

            UICaseType.TYPE_CONFIRMED -> {
                title.set(context.getString(R.string.affected))
                backgroundColor.set(getColor(R.color.background_confirmed))

            }
            UICaseType.TYPE_ACTIVE -> {
                title.set(context.getString(R.string.active))
                backgroundColor.set(getColor(R.color.background_active))
                confirmCaseModel?.let {
                    percentageCount.set(getPercentage(confirmCaseModel.totalCount, countModel.totalCount))
                }

            }
            UICaseType.TYPE_RECOVERED -> {
                title.set(context.getString(R.string.recovered))
                backgroundColor.set(getColor(R.color.background_recovered))
                confirmCaseModel?.let {
                    percentageCount.set(getPercentage(confirmCaseModel.totalCount, countModel.totalCount))
                }

            }
            UICaseType.TYPE_DEATH -> {
                title.set(context.getString(R.string.deaths))
                backgroundColor.set(getColor(R.color.background_deceased))
                confirmCaseModel?.let {
                    percentageCount.set(getPercentage(confirmCaseModel.totalCount, countModel.totalCount))
                }

            }
            UICaseType.TYPE_TESTING -> {
                title.set(context.getString(R.string.total_testing))
                currentCount.set("(+${Util.formatNumber(countModel.currentCount)})")
            }
        }
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    private fun getPercentage(confirmedCount: Int, matchingCount: Int): String {
        val percentage = (matchingCount.toFloat() / confirmedCount.toFloat()) * 100
        return "${"%.2f".format(percentage)}%"
    }
}
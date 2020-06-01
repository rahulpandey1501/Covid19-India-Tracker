package com.rpandey.covid19tracker_india.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.rpandey.covid19tracker_india.MainActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.StatusId
import com.rpandey.covid19tracker_india.data.processor.CovidIndiaSyncManager
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.model.CountModel
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.util.Util
import com.rpandey.covid19tracker_india.util.observeOnce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [OverallCasesWidgetConfigureActivity]
 */
class OverallCasesWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        if (context != null && appWidgetManager != null) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context == null || AppWidgetManager.ACTION_APPWIDGET_UPDATE != intent?.action)
            return

        CoroutineScope(Dispatchers.IO).launch {
            CovidIndiaSyncManager.getInstance().syncPrimaryData {
                if (it.statusId == StatusId.OVERALL_DATA) {
                    withContext(Dispatchers.Main) {
                        try { // try catch to avoid multiple callbacks because of multiple widgetIds
                            val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.INVALID_APPWIDGET_ID)
                            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                                updateAppWidget(context, AppWidgetManager.getInstance(context), widgetId)
                            }
                        } catch (e: Exception) {
                            updateAllAppWidget(context)
                        }

                        val userAction = intent.getBooleanExtra(USER_CLICK_SOURCE, false)
                        if (userAction) {
                            Toast.makeText(context, "updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val USER_CLICK_SOURCE = "USER_CLICK_SOURCE"
    }
}

internal fun updateAllAppWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val ids = appWidgetManager.getAppWidgetIds(
        ComponentName(
            context.applicationContext.packageName,
            OverallCasesWidget::class.java.name
        )
    )
    ids.forEach {
        updateAppWidget(context, appWidgetManager, it)
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    CoroutineScope(Dispatchers.Main).launch {

        val views = RemoteViews(context.packageName, R.layout.overall_cases_widget)
        views.setOnClickPendingIntent(
            R.id.root_view, PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java), 0
            )
        )

        val updateDataIntent =
            Intent(context.applicationContext, OverallCasesWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
                putExtra(OverallCasesWidget.USER_CLICK_SOURCE, true)
            }

        views.setOnClickPendingIntent(
            R.id.refresh, PendingIntent.getBroadcast(
                context, 0, updateDataIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        views.removeAllViews(R.id.overall_container)
        views.removeAllViews(R.id.district_container)

        val repository = CovidIndiaRepository(CovidDatabase.getInstance())

        val confirmView = RemoteViews(context.packageName, R.layout.item_cases_count_widget_0dp)
        confirmView.setTextViewText(R.id.confirm_title, context.getString(R.string.affected))
        views.addView(R.id.overall_container, confirmView)
        repository.getConfirmedCount().observeOnce {
            addCaseUI(confirmView, it)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Util.log("UpdateWidget", "Confirm Update call")
        }

        val deceasedView = RemoteViews(context.packageName, R.layout.item_cases_count_widget)
        deceasedView.setTextViewText(R.id.confirm_title, context.getString(R.string.deaths))
        views.addView(R.id.overall_container, deceasedView)
        repository.getDeceasedCount().observeOnce {
            addCaseUI(deceasedView, it)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Util.log("UpdateWidget", "Death Update call")
        }

        val recoverView = RemoteViews(context.packageName, R.layout.item_cases_count_widget_0dp)
        recoverView.setTextViewText(R.id.confirm_title, context.getString(R.string.recovered))
        views.addView(R.id.overall_container, recoverView)
        repository.getRecoveredCount().observeOnce {
            addCaseUI(recoverView, it)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Util.log("UpdateWidget", "Recover Update call")
        }

        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val heightCell = getNoOfHeightCell(options)
        if (heightCell > 1) {
            repository.getBookmarkedDistricts().observeOnce { districts ->
                if (districts.isNotEmpty()) {
                    views.setViewVisibility(R.id.divider,  View.VISIBLE)
                }
                districts.take(4).forEach {
                    val districtViews = RemoteViews(context.packageName, R.layout.item_cases_count_widget_0dp)
                    districtViews.setTextViewText(R.id.confirm_title, it.district)
                    addCaseUI(districtViews, CountModel(it.confirmed, it.getActive()))
                    views.addView(R.id.district_container, districtViews)
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        } else {
            views.setViewVisibility(R.id.divider,  View.GONE)
        }
    }
}

internal fun addCaseUI(childView: RemoteViews, countModel: CountModel) {
    childView.setTextViewText(
        R.id.confirm_current_count,
        Util.formatNumber(countModel.currentCount)
    )
    childView.setTextViewText(R.id.confirm_total_count, Util.formatNumber(countModel.totalCount))
}

private fun getNoOfWidthCell(newOptions: Bundle): Int {
    return ((floor(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH).toDouble() + 30) / 70).toInt())
}

private fun getNoOfHeightCell(newOptions: Bundle): Int {
//    return ((floor(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT).toDouble() + 30) / 70).toInt())
    return if (newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) > 70) 2 else 1
}
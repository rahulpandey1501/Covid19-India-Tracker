package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.model.covidIndia.Zone
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


object Util {

    fun formatNumber(number: Int): String {
        return DecimalFormat("##,##,##,###.##", DecimalFormatSymbols(Locale.US)).format(number)
    }

    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }

    fun timestampToDate(timestamp: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            val netDate = Date(timestamp)
            sdf.format(netDate)
        } catch (e: Exception) {
            ""
        }
    }

    fun getZoneColor(context: Context, zone: String?): Int {
        return ContextCompat.getColor(context, when(zone) {
            Zone.Green.name -> {
                R.color.zone_green
            }
            Zone.Orange.name -> {
                R.color.zone_orange
            }
            Zone.Red.name -> {
                R.color.zone_red
            }
            else -> android.R.color.transparent
        })
    }
}
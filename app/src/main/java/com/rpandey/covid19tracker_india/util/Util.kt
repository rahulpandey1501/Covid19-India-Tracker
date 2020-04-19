package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
}
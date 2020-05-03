package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
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

    fun shareAppIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getShareMessage())
            putExtra(Intent.EXTRA_STREAM, getUriFromAssets("share_banner.jpg"))
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun getUriFromAssets(fileName: String): Uri {
        return Uri.parse("content://${CovidApplication.INSTANCE.packageName}/$fileName")
    }

    fun getShareMessage(): String {
        return  "Get the latest updates on Covid19 \uD83D\uDE37cases across India on your mobile.\n" +
                "Made for India ❤️\n\n" +
                "Download link:- \n" +
                "${getShareUrl()}\n"
    }

    fun getShareUrl(): String {
        return PreferenceHelper.getString(Constants.KEY_SHARE_URL) ?: Constants.APP_SHARE_URL
    }
}
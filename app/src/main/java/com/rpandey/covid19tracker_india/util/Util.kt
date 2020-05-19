package com.rpandey.covid19tracker_india.util

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.view.WindowManager
import android.webkit.URLUtil
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.rpandey.covid19tracker_india.BuildConfig
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.data.Constants
import com.rpandey.covid19tracker_india.data.model.covidIndia.Zone
import com.rpandey.covid19tracker_india.util.customchrome.CustomTabsHelper
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt


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
            DateUtils.getRelativeTimeSpanString(
                timestamp,
                Calendar.getInstance().timeInMillis,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
        } catch (e: Exception) {
            "NA"
        }
    }

    fun getZoneColor(context: Context, zone: String?): Int {
        return ContextCompat.getColor(
            context, when (zone) {
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
            }
        )
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
        return "Get the latest updates on Covid19 \uD83D\uDE37cases across India on your mobile.\n" +
                "Made for India ❤️\n\n" +
                "Download link:- \n" +
                "${getShareUrl()}\n"
    }

    fun getShareUrl(): String {
        return PreferenceHelper.getString(Constants.KEY_SHARE_URL)
            ?: Constants.DEFAULT_APP_SHARE_URL
    }

    inline fun runWithExecutionTime(identifier: String, block: () -> Unit) {
        val before = System.currentTimeMillis()
        block()
        Log.d("Covid19", "$identifier execution time: ${System.currentTimeMillis() - before}ms")
    }

    fun openWebUrl(context: Context, url: String) {
        if (URLUtil.isValidUrl(url)) {
            try {
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setShowTitle(true)
                    .build()
                CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent)
                val packageName = CustomTabsHelper.getPackageNameToUse(context)
                if (packageName != null) {
                    customTabsIntent.intent.setPackage(packageName)
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                }
            } catch (e: Exception) {
                openBrowser(context, url)
            }
        }
    }

    fun openBrowser(context: Context, url: String) {
        if (URLUtil.isValidUrl(url)) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
    }

    /**
     * @returns Distance in KiloMeters
     */
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val p = 0.017453292519943295  // Math.PI / 180
        val a = 0.5 - cos((lat2 - lat1) * p) / 2 +
                cos(lat1 * p) * cos(lat2 * p) *
                (1 - cos((lon2 - lon1) * p)) / 2

        return 12742 * asin(sqrt(a)); // 2 * R; R = 6371 km
    }

    fun openCallIntent(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${number}")
        context.startActivity(intent)
    }

    fun getAppDownloadFileName(versionCode: Int): String {
        return "Covid19_India_Tracker_${versionCode}.apk"
    }

    fun getAppDownloadDirectory(): File {
        return File(CovidApplication.INSTANCE.externalCacheDir, "apk")
    }

    fun startInstallerIntent(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val fileUri = getFileUri(context, file)
        intent.setDataAndType(
            fileUri, "application/vnd.android.package-archive"
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        context.startActivity(intent)
    }

    fun log(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("Covid19 $tag", message)
        }
    }

    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
    }

    fun apkExist(versionCode: Int): Pair<Boolean, File> {
        val apkFile = File(getAppDownloadDirectory(), getAppDownloadFileName(versionCode))
        return (apkFile.exists() && apkFile.length() > 100) to apkFile
    }
}
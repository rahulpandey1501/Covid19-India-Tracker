package com.rpandey.covid19tracker_india

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.util.ThemeHelper

class CovidApplication : Application() {

    companion object {
        lateinit var INSTANCE: Context
        lateinit var analytics: FirebaseAnalytics
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = applicationContext
        StrictMode.setVmPolicy(VmPolicy.Builder().build()) // grant URI permission
        analytics = FirebaseAnalytics.getInstance(applicationContext)
        ThemeHelper.applyUserPrefTheme()
    }
}
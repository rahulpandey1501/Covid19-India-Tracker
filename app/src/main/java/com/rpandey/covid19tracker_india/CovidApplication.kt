package com.rpandey.covid19tracker_india

import android.app.Application
import android.content.Context
import com.rpandey.covid19tracker_india.util.ThemeHelper

class CovidApplication : Application() {

    companion object {
        lateinit var INSTANCE: Context
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = applicationContext
        ThemeHelper.applyUserPrefTheme()
    }
}
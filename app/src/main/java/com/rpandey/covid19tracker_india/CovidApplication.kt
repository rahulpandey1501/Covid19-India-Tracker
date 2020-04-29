package com.rpandey.covid19tracker_india

import android.app.Application
import com.rpandey.covid19tracker_india.util.ThemeHelper

class CovidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ThemeHelper.applyTheme(ThemeHelper.DEFAULT)
    }

}
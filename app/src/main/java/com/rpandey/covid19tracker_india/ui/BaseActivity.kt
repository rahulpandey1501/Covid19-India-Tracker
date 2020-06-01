package com.rpandey.covid19tracker_india.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.util.ThemeHelper

abstract class BaseActivity : AppCompatActivity() {

    val database by lazy { CovidDatabase.getInstance() }
    val repository by lazy { CovidIndiaRepository(database) }

    abstract fun getScreenName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            val currentTheme = ThemeHelper.getTheme(this)
            decor.systemUiVisibility = if (currentTheme == ThemeHelper.DARK_MODE) 0 else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        logEvent(getScreenName())
    }

    fun logEvent(event: String?) {
        event?.let { FirebaseAnalytics.getInstance(this).logEvent(event, null) }
    }
}
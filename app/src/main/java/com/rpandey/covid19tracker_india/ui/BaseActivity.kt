package com.rpandey.covid19tracker_india.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

abstract class BaseActivity : AppCompatActivity() {

    val database by lazy { CovidDatabase.getInstance() }
    val repository by lazy { CovidIndiaRepository(database) }

    abstract fun getScreenName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvent(getScreenName())
    }

    fun logEvent(event: String?) {
        event?.let { FirebaseAnalytics.getInstance(this).logEvent(event, null) }
    }
}
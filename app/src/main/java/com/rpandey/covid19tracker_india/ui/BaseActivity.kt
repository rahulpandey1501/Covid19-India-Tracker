package com.rpandey.covid19tracker_india.ui

import androidx.appcompat.app.AppCompatActivity
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

abstract class BaseActivity : AppCompatActivity() {

    val repository by lazy {
        CovidIndiaRepository(CovidDatabase.getInstance(this))
    }
}
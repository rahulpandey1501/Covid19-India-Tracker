package com.rpandey.covid19tracker_india.ui

import androidx.appcompat.app.AppCompatActivity
import com.rpandey.covid19tracker_india.data.processor.CovidIndiaDataProcessor
import com.rpandey.covid19tracker_india.data.repository.CovidIndiaRepository
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.NetworkBuilder

abstract class BaseActivity : AppCompatActivity() {

    val database by lazy { CovidDatabase.getInstance(this) }
    val apiProvider by lazy { APIProvider(NetworkBuilder.apiService, NetworkBuilder.firebaseHostService) }
    val repository by lazy { CovidIndiaRepository(database) }
    val dataProcessor by lazy { CovidIndiaDataProcessor(apiProvider, database) }
}
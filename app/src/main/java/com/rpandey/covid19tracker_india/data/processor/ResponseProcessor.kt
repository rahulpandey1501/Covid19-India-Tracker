package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

abstract class ResponseProcessor<T>(val covidDatabase: CovidDatabase) {

    abstract fun process(data: T)

}
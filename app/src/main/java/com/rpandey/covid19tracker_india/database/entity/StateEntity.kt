package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "states", primaryKeys = ["country", "code"])
data class StateEntity constructor(
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "population", defaultValue = "0") val population: Long
)
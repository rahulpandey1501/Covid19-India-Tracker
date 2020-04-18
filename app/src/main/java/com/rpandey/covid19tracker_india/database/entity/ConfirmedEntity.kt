package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "confirmed_cases",
    primaryKeys = ["date", "country", "state"]
)
data class ConfirmedEntity(
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "confirmed") val confirmed: Int,
    @ColumnInfo(name = "total_confirmed") var totalConfirmed: Int
)
package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "daily_changes",
    primaryKeys = ["order", "state"]
)
data class DailyChangesEntity(
    @ColumnInfo(name = "order") var order: Int = 0,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "state") val stateCode: String,
    @ColumnInfo(name = "confirmed") val confirmed: Int,
    @ColumnInfo(name = "total_confirmed") var totalConfirmed: Int,
    @ColumnInfo(name = "deceased") val deceased: Int,
    @ColumnInfo(name = "total_deceased") var totalDeceased: Int,
    @ColumnInfo(name = "recovered") val recovered: Int,
    @ColumnInfo(name = "total_recovered") var totalRecovered: Int,
    @ColumnInfo(name = "date") var date: String
)
package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_changes"
)
data class DailyChangesEntity(
    @PrimaryKey @ColumnInfo(name = "order") var order: Int = 0,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "confirmed") val confirmed: Int,
    @ColumnInfo(name = "total_confirmed") var totalConfirmed: Int,
    @ColumnInfo(name = "deceased") val deceased: Int,
    @ColumnInfo(name = "total_deceased") var totalDeceased: Int,
    @ColumnInfo(name = "recovered") val recovered: Int,
    @ColumnInfo(name = "total_recovered") var totalRecovered: Int,
    @ColumnInfo(name = "date") var date: String
)
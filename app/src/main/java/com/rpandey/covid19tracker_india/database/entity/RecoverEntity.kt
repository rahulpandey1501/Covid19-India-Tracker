package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "recovered_cases",
    primaryKeys = ["country", "state"]
)
data class RecoverEntity(
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "recovered") val recovered: Int,
    @ColumnInfo(name = "total_recovered") var totalRecovered: Int
)
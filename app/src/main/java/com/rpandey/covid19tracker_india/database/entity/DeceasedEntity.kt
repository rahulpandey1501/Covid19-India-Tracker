package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "deceased_cases",
    primaryKeys = ["country", "state"]
)
data class DeceasedEntity(
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "deceased") val deceased: Int,
    @ColumnInfo(name = "total_deceased") var totalDeceased: Int
)
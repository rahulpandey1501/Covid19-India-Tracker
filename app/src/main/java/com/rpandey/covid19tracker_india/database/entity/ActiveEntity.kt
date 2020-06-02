package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "active_cases",
    primaryKeys = ["country", "state"]
)
data class ActiveEntity(
    @ColumnInfo(name = "date", index = true) val date: Long,
    @ColumnInfo(name = "country", index = true) val country: String,
    @ColumnInfo(name = "state", index = true) val state: String,
    @ColumnInfo(name = "active") val active: Int?,
    @ColumnInfo(name = "current_active") val currentActive: Int?
)
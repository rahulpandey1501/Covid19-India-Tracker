package com.rpandey.covid19tracker_india.database.model

import androidx.room.ColumnInfo

data class CountModel(
    @ColumnInfo(name = "currentCount") val currentCount: Int,
    @ColumnInfo(name = "totalCount") val totalCount: Int)
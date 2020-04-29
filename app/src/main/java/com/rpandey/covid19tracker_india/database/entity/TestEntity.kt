package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "test",
    primaryKeys = ["stateName"]
)
data class TestEntity constructor(
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "stateName") val stateName: String,
    @ColumnInfo(name = "tested") val tested: Int,
    @ColumnInfo(name = "total_tested") val totalTested: Int
) {

    companion object {
        const val OVER_ALL = "OVER_ALL"
    }
}

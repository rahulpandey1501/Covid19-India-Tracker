package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "district_cases", primaryKeys = ["country", "stateName", "district"])
data class DistrictEntity constructor(
    @ColumnInfo(name = "districtId") val districtId: Int,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "stateName") val stateName: String,
    @ColumnInfo(name = "district") val district: String,
    @ColumnInfo(name = "confirmed") val confirmed: Int,
    @ColumnInfo(name = "total_confirmed") val totalConfirmed: Int,
    @ColumnInfo(name = "recovered") val recovered: Int,
    @ColumnInfo(name = "total_recovered") val totalRecovered: Int,
    @ColumnInfo(name = "deceased") val deceased: Int,
    @ColumnInfo(name = "total_deceased") val totalDeceased: Int
) {
    fun getActive() = (totalConfirmed - totalRecovered - totalDeceased)
}
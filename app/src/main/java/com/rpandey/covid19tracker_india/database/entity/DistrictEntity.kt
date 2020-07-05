package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "district_cases",
    primaryKeys = ["country", "stateName", "district"],
    indices = [Index(name = "index_district_cases", value = ["districtId", "district"])]
)
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
    @ColumnInfo(name = "total_deceased") val totalDeceased: Int,
    @ColumnInfo(name = "tested") val tested: Int,
    @ColumnInfo(name = "total_tested") val totalTested: Int,
    @ColumnInfo(name = "population") val population: Long,
    @ColumnInfo(name = "zone") val zone: String?
) {
    fun getActive() = (totalConfirmed - totalRecovered - totalDeceased)
    fun getCurrentActive() = (confirmed - recovered - deceased)
}

@Entity
data class DistrictUpdate(
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "stateName") val stateName: String,
    @ColumnInfo(name = "district") val district: String,
    @ColumnInfo(name = "zone") val zone: String
)
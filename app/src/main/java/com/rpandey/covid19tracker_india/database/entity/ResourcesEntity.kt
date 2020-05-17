package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    indices = [Index(name = "index_resources", value = ["stateName", "district", "category"])]
)
data class ResourcesEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "district") val district: String,
    @ColumnInfo(name = "stateName") val stateName: String,
    @ColumnInfo(name = "contact") val contact: String,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "organisation") val organisation: String
)
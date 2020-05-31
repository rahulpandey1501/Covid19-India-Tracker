package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "news"
)
data class NewsEntity(
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "source") val source: String? = null,
    @ColumnInfo(name = "headline") val headline: String,
    @ColumnInfo(name = "summary") val summary: String,
    @ColumnInfo(name = "image") val image: String? = null,
    @ColumnInfo(name = "link") val link: String? = null
) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
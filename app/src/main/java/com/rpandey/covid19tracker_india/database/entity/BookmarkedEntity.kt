package com.rpandey.covid19tracker_india.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "bookmark", primaryKeys = ["bookmark_id", "type"])
data class BookmarkedEntity(
    @ColumnInfo(name = "bookmark_id") val bookmarkId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "show_notification") val showNotification: Boolean = false
)

annotation class BookmarkType {
    companion object {
        const val STATE = "STATE"
        const val DISTRICT = "DISTRICT"
    }
}

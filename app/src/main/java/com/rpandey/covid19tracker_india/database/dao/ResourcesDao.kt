package com.rpandey.covid19tracker_india.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.ResourcesEntity

@Dao
interface ResourcesDao {

    @Query("DELETE FROM resources")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(resources: List<ResourcesEntity>)
}
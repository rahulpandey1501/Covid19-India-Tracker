package com.rpandey.covid19tracker_india.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.rpandey.covid19tracker_india.database.entity.TestEntity

@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entries: List<TestEntity>)
}
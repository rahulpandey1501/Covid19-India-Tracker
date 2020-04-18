package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.ActiveEntity

@Dao
interface ActiveDao {

    @Query("SELECT * FROM active_cases WHERE country = :country and state = :state order by date")
    fun getByState(country: String, state: String): LiveData<List<ActiveEntity>>

    @Query("SELECT * FROM active_cases WHERE country = :country and state = :state and date = :date")
    fun getByState(country: String, state: String, date: String): LiveData<ActiveEntity>

    @Query("SELECT * FROM active_cases WHERE country = :country")
    fun getAll(country: String): LiveData<List<ActiveEntity>>

    @Query("SELECT sum(active) FROM active_cases WHERE country = :country")
    fun getCurrentCount(country: String): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<ActiveEntity>)
}
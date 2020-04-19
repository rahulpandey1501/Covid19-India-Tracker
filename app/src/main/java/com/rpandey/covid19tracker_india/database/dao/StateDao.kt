package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.StateEntity

@Dao
interface StateDao {

    @Query("select * from states where country = :country order by name")
    fun getStates(country: String): LiveData<List<StateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<StateEntity>)
}
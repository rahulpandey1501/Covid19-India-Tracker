package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.DailyChangesEntity

@Dao
interface DailyChangesDao {

    @Query("select * from daily_changes where country = :country and state = :stateCode order by `order` desc limit :totalEntries")
    fun getEntries(country: String, stateCode: String, totalEntries: Int): LiveData<List<DailyChangesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<DailyChangesEntity>)

    @Query("delete from daily_changes")
    fun delete()
}
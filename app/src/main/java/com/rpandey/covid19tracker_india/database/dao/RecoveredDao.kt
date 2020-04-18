package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.RecoverEntity
import com.rpandey.covid19tracker_india.database.model.CountModel

@Dao
interface RecoveredDao {

    @Query("SELECT * FROM recovered_cases WHERE country = :country and state = :state order by date")
    fun getByState(country: String, state: String): LiveData<List<RecoverEntity>>

    @Query("SELECT * FROM recovered_cases WHERE country = :country and state = :state and date = :date")
    fun getByState(country: String, state: String, date: String): LiveData<RecoverEntity>

    @Query("SELECT * FROM recovered_cases WHERE country = :country")
    fun getAll(country: String): LiveData<List<RecoverEntity>>

    @Query("SELECT sum(total_recovered) as totalCount, sum(recovered) as currentCount FROM recovered_cases WHERE country = :country")
    fun getCurrentCount(country: String): LiveData<CountModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<RecoverEntity>)
}
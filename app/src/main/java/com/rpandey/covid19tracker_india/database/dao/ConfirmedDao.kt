package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.ConfirmedEntity
import com.rpandey.covid19tracker_india.database.model.CountModel

@Dao
interface ConfirmedDao {

    @Query("SELECT * FROM confirmed_cases WHERE country = :country and state = :state order by date")
    fun getByState(country: String, state: String): LiveData<List<ConfirmedEntity>>

    @Query("SELECT * FROM confirmed_cases WHERE country = :country and state = :state and date = :date")
    fun getByState(country: String, state: String, date: String): LiveData<ConfirmedEntity?>

    @Query("SELECT * FROM confirmed_cases WHERE country = :country")
    fun getAll(country: String): LiveData<List<ConfirmedEntity>>

    @Query("SELECT sum(total_confirmed) as totalCount, sum(confirmed) as currentCount FROM confirmed_cases WHERE country = :country")
    fun getCurrentCount(country: String): LiveData<CountModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<ConfirmedEntity>)

    @Query("delete from confirmed_cases")
    fun delete()
}
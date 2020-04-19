package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.DeceasedEntity
import com.rpandey.covid19tracker_india.database.model.CountModel

@Dao
interface DeceasedDao {

    @Query("SELECT * FROM deceased_cases WHERE country = :country and state = :state order by date")
    fun getByState(country: String, state: String): LiveData<List<DeceasedEntity>>

    @Query("SELECT * FROM deceased_cases WHERE country = :country and state = :state and date = :date")
    fun getByState(country: String, state: String, date: String): LiveData<DeceasedEntity?>

    @Query("SELECT * FROM deceased_cases WHERE country = :country")
    fun getAll(country: String): LiveData<List<DeceasedEntity>>

    @Query("SELECT sum(total_deceased) as totalCount, sum(deceased) as currentCount FROM deceased_cases WHERE country = :country")
    fun getCurrentCount(country: String): LiveData<CountModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<DeceasedEntity>)

    @Query("delete from deceased_cases")
    fun delete()
}
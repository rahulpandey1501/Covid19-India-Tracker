package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.database.entity.TestEntity
import com.rpandey.covid19tracker_india.database.model.CountModel

@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entries: List<TestEntity>)

    @Query("select total_tested as totalCount, tested as currentCount from test where country = :country and stateName = :stateName")
    fun getTotalCount(stateName: String, country: String = Country.INDIA.code): LiveData<CountModel>

    @Query("select total_vaccinated as totalCount, vaccinated as currentCount from test where country = :country and stateName = :stateName")
    fun getVaccinationCount(stateName: String, country: String = Country.INDIA.code): LiveData<CountModel>
}
package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity

@Dao
interface DistrictDao {

    @Query("select * from district_cases where country = :country and stateName = :stateName")
    fun getByState(country: String, stateName: String): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where country = :country")
    fun getAll(country: String): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where district like :district")
    fun getByDistrictName(district: String?): LiveData<List<DistrictEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<DistrictEntity>)

    @Query("DELETE FROM district_cases")
    fun delete()

}
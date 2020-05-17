package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
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

    @Query("SELECT * from resources")
    fun getAllResources(): LiveData<ResourcesEntity>

    @Query("SELECT DISTINCT stateName from resources order by stateName asc")
    fun getStates(): LiveData<List<String>>

    @Query("SELECT DISTINCT district from resources where stateName = :stateName order by district asc")
    fun getDistricts(stateName: String): LiveData<List<String>>

    @Query("SELECT DISTINCT category from resources where stateName = :stateName and district = :district order by category asc")
    fun getCategories(stateName: String, district: String): LiveData<List<String>>

    @Query("SELECT * from resources where stateName = :stateName and district = :district and category = :category order by id desc")
    fun getResources(stateName: String, district: String, category: String): LiveData<List<ResourcesEntity>>
}
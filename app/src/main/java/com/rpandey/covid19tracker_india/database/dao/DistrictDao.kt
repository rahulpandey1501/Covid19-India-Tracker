package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.entity.DistrictUpdate

@Dao
interface DistrictDao {

    @Query("select * from district_cases where country = :country and stateName = :stateName")
    fun getByState(country: String, stateName: String): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where country = :country and stateName = :stateName order by total_confirmed desc limit :count")
    fun getByState(country: String, stateName: String, count: Int): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where country = :country order by total_confirmed desc")
    fun getAll(country: String): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where country = :country order by total_confirmed desc limit :count")
    fun getDistricts(country: String, count: Int): LiveData<List<DistrictEntity>>

    @Query("select * from district_cases where district like :district")
    fun getByDistrictName(district: String?): List<DistrictEntity>

    @Query("select * from district_cases where districtId = :districtId")
    fun getByDistrictId(districtId: Int): LiveData<DistrictEntity>

    @Query("select max(CC.date) from confirmed_cases CC inner join states S on CC.state = S.code inner join district_cases DC on DC.stateName = S.name where districtId = :districtId")
    fun lastUpdatedTime(districtId: Int): LiveData<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<DistrictEntity>)

    @Update(entity = DistrictEntity::class)
    fun updateZone(data: List<DistrictUpdate>)

    @Query("DELETE FROM district_cases")
    fun delete()

    @Query("SELECT * from district_cases where confirmed > 0 order by confirmed desc")
    fun newConfirmedCases(): LiveData<List<DistrictEntity>>

    @Query("SELECT * from district_cases where stateName = :stateName and confirmed > 0 order by confirmed desc")
    fun newConfirmedCases(stateName: String): LiveData<List<DistrictEntity>>

}
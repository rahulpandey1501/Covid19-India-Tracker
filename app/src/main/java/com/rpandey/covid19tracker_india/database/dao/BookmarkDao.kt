package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpandey.covid19tracker_india.database.entity.BookmarkType
import com.rpandey.covid19tracker_india.database.entity.BookmarkedEntity
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity

@Dao
interface BookmarkDao {

    @Query("select * from bookmark where type = :type")
    fun getBookmark(@BookmarkType type: String): List<BookmarkedEntity>

    @Query("select * from bookmark")
    fun getAllBookmark(): List<BookmarkedEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addBookmark(bookmarkedEntity: BookmarkedEntity)

    @Query("delete from bookmark where bookmark_id = :bookmarkId and type = :type")
    fun deleteBookmark(bookmarkId: String, type: String)

    @Query("update bookmark set show_notification = :showNotification where bookmark_id = :bookmarkId and type = :type")
    fun updateNotification(bookmarkId: String, type: String, showNotification: Boolean)

    @Delete
    fun deleteBookmark(bookmarkedEntity: BookmarkedEntity)

    @Query("select * from district_cases inner join bookmark on districtId = bookmark_id order by district_cases.total_confirmed desc")
    fun getDistricts(): LiveData<List<DistrictEntity>>

    @Query("select * from bookmark where type = :type")
    fun getBookmarkByType(type: String): LiveData<List<BookmarkedEntity>>

    @Query("select CC.state state, S.name stateName, CC.date date, CC.confirmed confirmedCases, CC.total_confirmed totalConfirmedCases, AC.active activeCases, RC.recovered recoveredCases, RC.total_recovered totalRecoveredCases, DC.deceased deceasedCases, DC.total_deceased totalDeceasedCases from confirmed_cases as CC inner join active_cases as AC on CC.state == AC.state inner join recovered_cases as RC on CC.state == RC.state inner join deceased_cases as DC on CC.state == DC.state inner join states as S on S.code == CC.state inner join bookmark BM on CC.state == BM.bookmark_id order by CC.total_confirmed desc")
    fun getCombinedCases(): LiveData<List<CombinedCasesModel>>

}
package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

@Dao
interface CombinedCasesDao {

    @Query("select CC.state state, S.name stateName, CC.date date, CC.confirmed confirmedCases, CC.total_confirmed totalConfirmedCases, AC.active activeCases, RC.recovered recoveredCases, RC.total_recovered totalRecoveredCases, DC.deceased deceasedCases, DC.total_deceased totalDeceasedCases from confirmed_cases as CC inner join active_cases as AC on CC.state == AC.state inner join recovered_cases as RC on CC.state == RC.state inner join deceased_cases as DC on CC.state == DC.state inner join states as S on S.code == CC.state order by CC.total_confirmed desc")
    fun getOverall(): LiveData<List<CombinedCasesModel>>

    @Query("select CC.state state, S.name stateName, CC.date date, CC.confirmed confirmedCases, CC.total_confirmed totalConfirmedCases, AC.active activeCases, RC.recovered recoveredCases, RC.total_recovered totalRecoveredCases, DC.deceased deceasedCases, DC.total_deceased totalDeceasedCases from confirmed_cases as CC inner join active_cases as AC on CC.state == AC.state inner join recovered_cases as RC on CC.state == RC.state inner join deceased_cases as DC on CC.state == DC.state inner join states as S on S.code == CC.state order by CC.confirmed desc")
    fun getOverallNewCases(): LiveData<List<CombinedCasesModel>>

    @Query("select CC.state state, S.name stateName, CC.date date, CC.confirmed confirmedCases, CC.total_confirmed totalConfirmedCases, AC.active activeCases, RC.recovered recoveredCases, RC.total_recovered totalRecoveredCases, DC.deceased deceasedCases, DC.total_deceased totalDeceasedCases from confirmed_cases as CC inner join active_cases as AC on CC.state == AC.state inner join recovered_cases as RC on CC.state == RC.state inner join deceased_cases as DC on CC.state == DC.state inner join states as S on S.code == CC.state where S.name like :stateName")
    fun searchOverall(stateName: String): List<CombinedCasesModel>

    @Query("select CC.state state, S.name stateName, CC.date date, CC.confirmed confirmedCases, CC.total_confirmed totalConfirmedCases, AC.active activeCases, RC.recovered recoveredCases, RC.total_recovered totalRecoveredCases, DC.deceased deceasedCases, DC.total_deceased totalDeceasedCases from confirmed_cases as CC inner join active_cases as AC on CC.state == AC.state inner join recovered_cases as RC on CC.state == RC.state inner join deceased_cases as DC on CC.state == DC.state inner join states as S on S.code == CC.state where CC.state = :state")
    fun getOverall(state: String): LiveData<CombinedCasesModel>

}

data class CombinedCasesModel(
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "stateName") val stateName: String,
    @ColumnInfo(name = "activeCases") val activeCases: Int,
    @ColumnInfo(name = "confirmedCases") val confirmedCases: Int,
    @ColumnInfo(name = "totalConfirmedCases") val totalConfirmedCases: Int,
    @ColumnInfo(name = "recoveredCases") val recoveredCases: Int,
    @ColumnInfo(name = "totalRecoveredCases") val totalRecoveredCases: Int,
    @ColumnInfo(name = "deceasedCases") val deceasedCases: Int,
    @ColumnInfo(name = "totalDeceasedCases") val totalDeceasedCases: Int
)
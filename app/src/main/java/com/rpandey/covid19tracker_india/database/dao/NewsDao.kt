package com.rpandey.covid19tracker_india.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpandey.covid19tracker_india.database.entity.NewsEntity

@Dao
interface NewsDao {

    @Query("select * from news where country = :country")
    fun getNews(country: String): LiveData<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<NewsEntity>)

    @Query("delete from news")
    fun delete()
}
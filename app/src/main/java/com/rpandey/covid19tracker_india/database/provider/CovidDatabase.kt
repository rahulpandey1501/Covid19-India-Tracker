package com.rpandey.covid19tracker_india.database.provider

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rpandey.covid19tracker_india.database.dao.*
import com.rpandey.covid19tracker_india.database.entity.*

@Database(
    entities = [
        ActiveEntity::class,
        ConfirmedEntity::class,
        DeceasedEntity::class,
        RecoverEntity::class,
        StateEntity::class,
        DistrictEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class CovidDatabase : RoomDatabase() {

    companion object {

        private var database: CovidDatabase? = null

        fun getInstance(context: Context): CovidDatabase {
            database = database ?: Room.databaseBuilder(context.applicationContext, CovidDatabase::class.java, "covid_database")
                .addMigrations()
                .build()
            return database!!
        }
    }

    abstract fun activeDao(): ActiveDao
    abstract fun confirmedDao(): ConfirmedDao
    abstract fun recoveredDao(): RecoveredDao
    abstract fun deceasedDao(): DeceasedDao
    abstract fun stateDao(): StateDao
    abstract fun districtDao(): DistrictDao
}
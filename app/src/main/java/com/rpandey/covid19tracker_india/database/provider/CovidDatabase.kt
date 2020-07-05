package com.rpandey.covid19tracker_india.database.provider

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rpandey.covid19tracker_india.CovidApplication
import com.rpandey.covid19tracker_india.database.dao.*
import com.rpandey.covid19tracker_india.database.entity.*
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase.Companion.VERSION

@Database(
    entities = [
        ActiveEntity::class,
        ConfirmedEntity::class,
        DeceasedEntity::class,
        RecoverEntity::class,
        StateEntity::class,
        DistrictEntity::class,
        BookmarkedEntity::class,
        TestEntity::class,
        ResourcesEntity::class,
        NewsEntity::class,
        DailyChangesEntity::class
    ],
    exportSchema = false,
    version = VERSION
)
abstract class CovidDatabase : RoomDatabase() {

    companion object {

        const val VERSION = 9
        private const val NAME = "covid_database"

        private var database: CovidDatabase? = null

        fun getInstance(): CovidDatabase {
            database = database ?: Room.databaseBuilder(CovidApplication.INSTANCE, CovidDatabase::class.java, NAME)
                .addMigrations(
                    Migrations1to2(),
                    Migrations2to3(),
                    Migrations3to4(),
                    Migrations4to5(),
                    Migrations5to6(),
                    Migrations6to7(),
                    Migrations7to8(),
                    Migrations8to9()
                ).build()
            return database!!
        }
    }

    abstract fun activeDao(): ActiveDao
    abstract fun confirmedDao(): ConfirmedDao
    abstract fun recoveredDao(): RecoveredDao
    abstract fun deceasedDao(): DeceasedDao
    abstract fun stateDao(): StateDao
    abstract fun districtDao(): DistrictDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun combinedCasesDao(): CombinedCasesDao
    abstract fun testDao(): TestDao
    abstract fun resourceDao(): ResourcesDao
    abstract fun newsDao(): NewsDao
    abstract fun dailyChangesDao(): DailyChangesDao
}
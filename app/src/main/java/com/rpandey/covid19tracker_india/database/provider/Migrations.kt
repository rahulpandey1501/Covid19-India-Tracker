package com.rpandey.covid19tracker_india.database.provider

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun isColumnExist(db: SupportSQLiteDatabase, tableName: String, columnName: String):Boolean{
    val cursor = db.query("SELECT * from $tableName limit 0")
    val isExists = cursor.getColumnIndex(columnName) != -1
    cursor.close()
    return isExists
}

fun addColumn(db: SupportSQLiteDatabase, tableName: String, columnName:String, columnType:String){
    if(isColumnExist(db, tableName, columnName)){
        return
    }
    db.execSQL("ALTER TABLE $tableName ADD  $columnName $columnType")
}

class Migrations1to2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addColumn(database, "district_cases", "confirmed", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "recovered", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "total_recovered", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "deceased", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "total_deceased", "INTEGER NOT NULL DEFAULT 0")
    }
}

class Migrations2to3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `test` (`date` INTEGER NOT NULL, `country` TEXT NOT NULL, `stateName` TEXT NOT NULL, `tested` INTEGER NOT NULL, `total_tested` INTEGER NOT NULL, PRIMARY KEY(`stateName`))")
    }
}

class Migrations3to4: Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addColumn(database, "district_cases", "zone", "TEXT DEFAULT NULL")
    }
}

class Migrations4to5: Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `resources` (`id` INTEGER NOT NULL, `category` TEXT NOT NULL, `district` TEXT NOT NULL, `stateName` TEXT NOT NULL, `contact` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `description` TEXT NOT NULL, `organisation` TEXT NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("CREATE INDEX `index_resources` ON `resources` (`stateName`, `district`, `category`)")
        database.execSQL("CREATE INDEX `index_district_cases` ON `district_cases` (`districtId`, `district`)")
    }
}

class Migrations5to6: Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `news` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `country` TEXT NOT NULL, `source` TEXT, `headline` TEXT NOT NULL, `summary` TEXT NOT NULL, `image` TEXT, `link` TEXT)")
        database.execSQL("CREATE TABLE `daily_changes` (`order` INTEGER NOT NULL, `country` TEXT NOT NULL, `confirmed` INTEGER NOT NULL, `total_confirmed` INTEGER NOT NULL, `deceased` INTEGER NOT NULL, `total_deceased` INTEGER NOT NULL, `recovered` INTEGER NOT NULL, `total_recovered` INTEGER NOT NULL, `date` TEXT NOT NULL, PRIMARY KEY(`order`))")
    }
}

class Migrations6to7: Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addColumn(database, "active_cases", "current_active", "INTEGER DEFAULT 0")
    }
}

class Migrations7to8: Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addColumn(database, "district_cases", "tested", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "total_tested", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "population", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "states", "population", "INTEGER NOT NULL DEFAULT 0")
    }
}

class Migrations8to9: Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `daily_changes`")
        database.execSQL("CREATE TABLE `daily_changes` (`order` INTEGER NOT NULL, `country` TEXT NOT NULL, `state` TEXT NOT NULL, `confirmed` INTEGER NOT NULL, `total_confirmed` INTEGER NOT NULL, `deceased` INTEGER NOT NULL, `total_deceased` INTEGER NOT NULL, `recovered` INTEGER NOT NULL, `total_recovered` INTEGER NOT NULL, `date` TEXT NOT NULL, PRIMARY KEY(`order`, `state`))")
    }
}

class Migrations9to10: Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        addColumn(database, "test", "vaccinated", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "test", "total_vaccinated", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "vaccinated", "INTEGER NOT NULL DEFAULT 0")
        addColumn(database, "district_cases", "total_vaccinated", "INTEGER NOT NULL DEFAULT 0")
    }
}
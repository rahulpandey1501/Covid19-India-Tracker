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
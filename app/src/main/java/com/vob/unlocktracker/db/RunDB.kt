package com.vob.unlocktracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [Run::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunDB: RoomDatabase() {

    abstract fun getRunDao(): RunDAO

}
package com.example.runningapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [RunDto::class], version = 1)
abstract class RunningDatabase:RoomDatabase() {
    abstract fun dao(): RunDao
}
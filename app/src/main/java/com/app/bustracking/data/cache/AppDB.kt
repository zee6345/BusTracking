package com.app.bustracking.data.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.bustracking.app.BusTracking

import com.app.bustracking.data.responseModel.Travel

@Database(entities = [Travel::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {

    abstract fun travelDao(): TravelDao

    companion object {
        fun init(context: Context): AppDB {
            return Room.databaseBuilder(context, AppDB::class.java, "AppDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }

}
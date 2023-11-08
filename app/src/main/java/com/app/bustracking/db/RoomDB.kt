package com.app.bustracking.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.bustracking.db.converters.Converters
import com.app.bustracking.db.dao.RouteDao
import com.app.bustracking.db.dao.TravelDao
import com.app.bustracking.db.dao.TravelRouteRelationDao
import com.app.bustracking.db.entities.RouteEntity
import com.app.bustracking.db.entities.TravelEntity
import com.app.bustracking.db.entities.TravelRouteRelation

@Database(entities = [TravelEntity::class, RouteEntity::class, TravelRouteRelation::class], version = 1, exportSchema = false)

@TypeConverters(Converters::class)
abstract class RoomDB : RoomDatabase() {

    abstract fun travelDao(): TravelDao
    abstract fun routeDao(): RouteDao
    abstract fun travelRouteRelationDao(): TravelRouteRelationDao
    companion object {
        private var INSTANCE: RoomDB? = null

        fun getInstance(context: Context): RoomDB? {
            if (INSTANCE == null) {
                synchronized(RoomDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        RoomDB::class.java, "user.db").allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
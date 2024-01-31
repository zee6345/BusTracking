package com.app.bustracking.data.local;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;
import com.app.bustracking.data.responseModel.Travel;

@androidx.room.Database(entities = {Route.class, Stop.class, Travel.class}, version = 5, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class Database extends RoomDatabase {

    public abstract StopsDao stopsDao();
    public abstract TravelDao travelDao();
    public abstract RoutesDao routesDao();

    public static Database init(Context context) {
        return Room.databaseBuilder(context, Database.class, "AppDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

}

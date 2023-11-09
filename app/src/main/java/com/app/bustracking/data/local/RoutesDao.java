package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface RoutesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Route route);

    @Query("select * from route")
    List<Route> fetchRoutes();

    @Query("select * from route where travel_id=:travelId")
    Route fetchRoute(int travelId);

}

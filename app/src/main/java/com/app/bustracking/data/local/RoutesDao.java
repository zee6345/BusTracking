package com.app.bustracking.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.bustracking.data.responseModel.Route;

import java.util.List;

@Dao
public interface RoutesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Route route);

    @Query("select * from route")
    List<Route> fetchAllRoutes();

    @Query("select * from route where isFavourite=0")
    LiveData<List<Route>> fetchRoutes();

    @Query("select * from route where isFavourite=1")
    LiveData<List<Route>> fetchFavouriteRoutes();

    @Query("select * from route where isFavourite=1")
    List<Route> fetchFavouriteRouteList();

    @Query("select * from route where isFavourite=0")
    List<Route> fetchRoutesList();

    @Query("select * from route where routeId=:routeId")
    Route fetchRouteById(int routeId);

    @Query("select * from route where bus_id=:busId")
    List<Route> fetchRouteByBusId(int busId);

    @Query("select bus_id from route where agency_id=:agencyId")
    int fetchBusId(int agencyId);

    @Query("select * from route where routeId=:travelId")
    Route fetchRoute(int travelId);

    @Query("select color from route where travel_id=:travelId")
    String fetchRouteColor(int travelId);

    @Query("update route set isFavourite=:favourite where routeId=:travelId and isFavourite=0")
    void addFavourite(int travelId, int favourite);

    @Query("update route set isFavourite=:favourite where routeId=:travelId and isFavourite=1")
    void removeFavourite(int travelId, int favourite);

    @Update
    void updateFav(Route route);

    @Update
    void updateVehicleStatus(Route route);

}

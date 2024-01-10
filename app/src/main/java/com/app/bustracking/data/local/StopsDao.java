package com.app.bustracking.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface StopsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Stop stop);

    @Query("select * from Stop where isFavourite=0 and agency_id=:agencyId")
    LiveData<List<Stop>> fetchStops(int agencyId);

    @Query("select * from Stop where isFavourite=1 and agency_id=:agencyId")
    LiveData<List<Stop>> fetchFavouriteStops(int agencyId);

    @Query("select * from Stop where isFavourite=1")
    LiveData<List<Stop>> fetchAllFavouriteStops();

    @Query("select * from Stop where route_id=:routeId")
    LiveData<List<Stop>> fetchAllStopsWithObserver(int routeId);

    @Query("select * from Stop where stopId=:stopId")
    Stop fetchStop(int stopId);

    @Query("select route_id from stop where stopId=:stopId")
    int fetchRouteId(int stopId);

    @Query("select stopId from stop where lat=:lat and lng=:lng")
    int stopIdByLatLng(double lat, double lng);

    @Query("select * from stop where lat=:lat and lng=:lng")
    Stop stopByLatLng(double lat, double lng);

    @Update
    void updateStop(Stop stop);


}

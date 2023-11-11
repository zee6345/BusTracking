package com.app.bustracking.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface StopsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Stop stop);


    @Query("select * from Stop where agency_id=:agencyId")
    List<Stop> fetchAllStops(int agencyId);

    @Query("select * from Stop where isFavourite=0 and agency_id=:agencyId")
    LiveData<List<Stop>> fetchStops(int agencyId);

    @Query("select * from Stop where isFavourite=1 and agency_id=:agencyId")
    LiveData<List<Stop>> fetchFavouriteStops(int agencyId);

    @Query("select * from Stop where stopId=:stopId")
    Stop fetchStop(int stopId);

    @Query("update Stop set isFavourite=:favourite where stopId=:stopId and isFavourite=0")
    void addFavourite(int stopId, int favourite);

    @Query("update Stop set isFavourite=:favourite where stopId=:stopId and isFavourite=1")
    void removeFavourite(int stopId, int favourite);


}

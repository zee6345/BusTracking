package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.GetTravelList;
import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface StopsDao {

    @Insert
    void insert(Stop stop);

    @Query("select * from stops")
    List<Stop> getAllStops();


}

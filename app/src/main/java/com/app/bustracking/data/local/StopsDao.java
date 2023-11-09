package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface StopsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stop stop);

    @Query("select * from stop")
    List<Stop> fetchStops();
}

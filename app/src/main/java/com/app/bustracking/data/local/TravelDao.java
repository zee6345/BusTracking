package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.Travel;

import java.util.List;

@Dao
public interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Travel travel);

    @Query("select * from Travel")
    List<Travel> fetchTravels();

}

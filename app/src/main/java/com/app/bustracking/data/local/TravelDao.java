package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;

import com.app.bustracking.data.responseModel.Travel;

@Dao
public interface TravelDao {
    @Insert
    void insert(Travel travel);

}

package com.app.bustracking.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.bustracking.data.responseModel.Route;
import com.app.bustracking.data.responseModel.Stop;

import java.util.List;

@Dao
public interface RoutesDao {

    @Insert
    void insert(Route route);



}

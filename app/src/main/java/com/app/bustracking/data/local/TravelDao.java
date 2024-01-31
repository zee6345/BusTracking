package com.app.bustracking.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.bustracking.data.responseModel.Travel;

import java.util.List;

@Dao
public interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Travel travel);

    @Query("select travel_name from travel where travelId=:travelId")
    String travelName(int travelId);

//    @Query("select * from Travel where isFavourite=0 and agency_id=:agencyId")
//    LiveData<List<Travel>> fetchTravels(int agencyId);
//
//    @Query("Select * from Travel where agency_id=:agencyID ")
//    List<Travel> fetchAllTravels(int agencyID);
//    @Query("select * from Travel where isFavourite=1 and agency_id=:agencyId")
//    LiveData<List<Travel>> fetchFavouriteTravels(int agencyId);
//
//    @Query("update travel set isFavourite=:favourite where travelId=:travelId and isFavourite=0")
//    void addFavourite(int travelId, int favourite);
//
//    @Query("update travel set isFavourite=:favourite where travelId=:travelId and isFavourite=1")
//    void removeFavourite(int travelId, int favourite);
//
//    @Update
//    void updateFavourite(Travel travel);

}

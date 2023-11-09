package com.app.bustracking.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.bustracking.data.responseModel.GetTravelList

@Dao
interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTravel(getTravelList: GetTravelList)


}
package com.app.bustracking.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.bustracking.db.entities.TravelRouteRelation


@Dao
interface TravelRouteRelationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: TravelRouteRelation)


}
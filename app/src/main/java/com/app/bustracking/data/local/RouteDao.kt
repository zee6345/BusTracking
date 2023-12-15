package com.app.bustracking.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.bustracking.data.responseModel.Route

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(route: Route?)

    @Query("select * from route")
    fun fetchAllRoutes(): List<Route?>?

    @Query("select * from route where isFavourite=0")
    suspend fun fetchRoutes(): List<Route>

    @Query("select * from route where isFavourite=1")
    suspend fun fetchFavouriteRoutes(): List<Route>

    @Query("select * from route where routeId=:routeId")
    fun fetchRouteById(routeId: Int): Route?

    @Query("select bus_id from route where agency_id=:agencyId")
    fun fetchBusId(agencyId: Int): Int

    @Query("select * from route where routeId=:travelId")
    fun fetchRoute(travelId: Int): Route?

    @Query("select color from route where travel_id=:travelId")
    fun fetchRouteColor(travelId: Int): String?

    @Query("update route set isFavourite=:favourite where routeId=:travelId and isFavourite=0")
    fun addFavourite(travelId: Int, favourite: Int)

    @Query("update route set isFavourite=:favourite where routeId=:travelId and isFavourite=1")
    fun removeFavourite(travelId: Int, favourite: Int)

    @Update
    fun updateFav(route: Route?)
}
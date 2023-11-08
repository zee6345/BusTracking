package com.app.bustracking.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.bustracking.data.responseModel.Stop

@Entity(tableName = "route_list")
data class RouteEntity(
    @PrimaryKey
    val id: Int,
    val route_title: String,
    val description: String,
    val agency_id: Int,
    val bus_id: Int,
    val color: String,
    val created_at: String,
    val direction_id: Any,
    val latitude: Int,
    val longitude: Int,
    val stop: List<Stop>,
    val travel_id: Int,
    val trip_distance: String,
    val type: String,
    val updated_at: String
)
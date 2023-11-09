package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "routes")
data class Route(
    val agency_id: Int? = null,
    val bus_id: Int? = null,
    val color: String? = null,
    val created_at: String? = null,
    val description: String? = null,
    val direction_id: Int? = null,
    val id: Int,
    val latitude: Int? = null,
    val longitude: Int? = null,
    val route_title: String? = null,
    val stop: List<Stop>,
    val travel_id: Int? = null,
    val trip_distance: String,
    val type: String? = null,
    val updated_at: String? = null,
    @PrimaryKey(autoGenerate = true) val autoid: Int
) : Serializable
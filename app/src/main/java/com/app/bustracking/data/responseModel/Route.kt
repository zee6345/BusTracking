package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Route(
    @SerializedName("id")
    @PrimaryKey
    val routeId: Int,
    val agency_id: Int? = null,
    val bus_id: Int? = null,
    val color: String? = null,
    val created_at: String? = null,
    val description: String? = null,
    val direction_id: Int? = null,
    val latitude: Int? = null,
    val longitude: Int? = null,
    val route_title: String? = null,
    val stop: List<Stop>,
    val travel_id: Int? = null,
    val trip_distance: String,
    val type: String? = null,
    val updated_at: String? = null,
    var isFavourite: Boolean = false,
    var isVehicleConnected:Boolean = false
) : Serializable{
    override fun toString(): String {
        return "Route(routeId=$routeId, agency_id=$agency_id, bus_id=$bus_id, color=$color, created_at=$created_at, description=$description, direction_id=$direction_id, latitude=$latitude, longitude=$longitude, route_title=$route_title, stop=$stop, travel_id=$travel_id, trip_distance='$trip_distance', type=$type, updated_at=$updated_at, isFavourite=$isFavourite, isVehicleConnected=$isVehicleConnected)"
    }
}
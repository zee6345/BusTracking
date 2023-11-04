package com.app.bustracking.data.responseModel

import java.io.Serializable

data class Route(
    val agency_id: Int,
    val bus_id: Int,
    val color: String,
    val created_at: String,
    val description: String,
    val direction_id: Any,
    val id: Int,
    val latitude: Int,
    val longitude: Int,
    val route_title: String,
    val stop: List<Stop>,
    val travel_id: Int,
    val trip_distance: String,
    val type: String,
    val updated_at: String
):Serializable
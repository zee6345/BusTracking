package com.app.bustracking.presentation.model

data class LocationEvent(
    val event: String,
    val data: LocationData,
    val channel: String
)

data class LocationData(
    val location: LocationInfo
)

data class LocationInfo(
    val lat: Double,
    val long: Double,
    val bus_id: Int,
    val route_id: Int,
    val bus_number_plate: Int,
    val travel_id: Int,
    val bus_status: Int
)

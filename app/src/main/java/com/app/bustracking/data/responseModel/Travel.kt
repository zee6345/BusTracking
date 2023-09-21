package com.app.bustracking.data.responseModel

data class Travel(
    val agency_id: Int,
    val bus_id: Any,
    val bus_number_plate: Any,
    val created_at: String,
    val driver_id: Any,
    val id: Int,
    val matricule: Any,
    val travel_arrival_time: String,
    val travel_departure_time: String,
    val travel_description: String,
    val travel_name: String,
    val trip_id: Any,
    val updated_at: String,
    val user_id: Any
)
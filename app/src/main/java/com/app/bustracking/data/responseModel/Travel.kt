package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Travel(
    val agency_id: Int,
    val bus_id: String,
    val bus_number_plate: String,
    val created_at: String,
    val driver_id: String,
    @PrimaryKey val id: Int,
    val matricule: String,
    val travel_arrival_time: String,
    val travel_departure_time: String,
    val travel_description: String,
    val travel_name: String,
    val trip_id: String,
    val updated_at: String,
    val user_id: String
)
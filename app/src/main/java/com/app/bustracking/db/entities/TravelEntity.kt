package com.app.bustracking.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_list")
data class TravelEntity (

    @PrimaryKey
    val id: Int,
    val travel_name: String,
    val travel_description: String,

    val agency_id: Int,
    val bus_id: Any,
    val bus_number_plate: Any,
    val created_at: String,
    val driver_id: Any,
    val matricule: Any,
    val travel_arrival_time: String,
    val travel_departure_time: String,
    val trip_id: Any,
    val updated_at: String,
    val user_id: Any
)
package com.app.bustracking.data.responseModel

import androidx.room.Entity

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Travel(
    @SerializedName("id")
    @PrimaryKey
    val travelId: Int,
    val agency_id: Int? = null,
    val bus_id: Int? = null,
    val bus_number_plate: String? = null,
    val created_at: String? = null,
    val driver_id: Int? = null,
    val matricule: String? = null,
    val travel_arrival_time: String? = null,
    val travel_departure_time: String? = null,
    val travel_description: String? = null,
    val travel_name: String? = null,
    val trip_id: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
)
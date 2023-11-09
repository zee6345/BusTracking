package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Stop(
    @SerializedName("id")
    @PrimaryKey
    val stopId: Int,
    val agency_id: Int? = null,
    val created_at: String? = null,
    val direction: String? = null,
    val lat: String? = null,
    val lng: String? = null,
    val route_id: Int? = null,
    val stop_time: String? = null,
    val stop_title: String? = null,
    val updated_at: String? = null,
    val isFavourite:Boolean = false
)
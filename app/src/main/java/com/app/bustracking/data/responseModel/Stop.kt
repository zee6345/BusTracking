package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class Stop(
    val agency_id: Int? = null,
    val created_at: String? = null,
    val direction: String? = null,
    val id: Int,
    val lat: String? = null,
    val lng: String? = null,
    val route_id: Int? = null,
    val stop_time: String? = null,
    val stop_title: String? = null,
    val updated_at: String? = null,
    @PrimaryKey(autoGenerate = true) val autoId: Int,
)
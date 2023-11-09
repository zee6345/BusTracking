package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "get_travel_lists")
data class GetTravelList(
    @PrimaryKey
    val id: Int,
    val flag: String,
    val travel_list: List<Travel>
)
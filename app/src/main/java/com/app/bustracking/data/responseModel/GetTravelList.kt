package com.app.bustracking.data.responseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class GetTravelList(
    val flag: String,
    val travel_list: List<Travel>
)
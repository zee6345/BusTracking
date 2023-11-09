package com.app.bustracking.data.responseModel

import androidx.room.PrimaryKey
import androidx.room.Relation

data class GetTravelList(
    val flag: String,
    @Relation(parentColumn = "flag", entityColumn = "id")
    val travel_list: List<Travel>
)
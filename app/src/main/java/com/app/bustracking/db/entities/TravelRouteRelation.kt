package com.app.bustracking.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_route_relation")
data class TravelRouteRelation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val travelId: Int,
    val routeId: Int
)
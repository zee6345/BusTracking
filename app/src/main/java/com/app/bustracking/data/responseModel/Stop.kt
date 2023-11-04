package com.app.bustracking.data.responseModel

data class Stop(
    val agency_id: Int,
    val created_at: String,
    val direction: String,
    val id: Int,
    val lat: String,
    val lng: String,
    val route_id: Int,
    val stop_time: String,
    val stop_title: String,
    val updated_at: String
)
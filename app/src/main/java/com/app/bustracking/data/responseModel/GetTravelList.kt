package com.app.bustracking.data.responseModel

data class GetTravelList(
    val flag: String,
    val travel_list: List<Travel>
)
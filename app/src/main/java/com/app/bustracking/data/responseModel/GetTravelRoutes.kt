package com.app.bustracking.data.responseModel

data class GetTravelRoutes(
    val flag: String,
    val route_list: List<Route>
)
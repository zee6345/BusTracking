package com.app.bustracking.data.responseModel

data class GetBusList(
    val bus_list: List<Bus>,
    val flag: String
)
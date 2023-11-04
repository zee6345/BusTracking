package com.app.bustracking.data.responseModel

data class Bus(
    val agency_id: Int,
    val bus_no: String,
    val color: Any,
    val created_at: String,
    val id: Int,
    val model: String,
    val name: String,
    val updated_at: String,
    val user_id: String
)
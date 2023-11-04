package com.app.bustracking.data.responseModel

data class GetAgenciesList(
    val agency_list: List<Agency>,
    val flag: String
)
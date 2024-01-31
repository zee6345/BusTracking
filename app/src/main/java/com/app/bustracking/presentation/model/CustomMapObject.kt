package com.app.bustracking.presentation.model

import com.mapbox.mapboxsdk.geometry.LatLng

data class CustomMapObject(
    val routeId:Int,
    val latLng :LatLng

)

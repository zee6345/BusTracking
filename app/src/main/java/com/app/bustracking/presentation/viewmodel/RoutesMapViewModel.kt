package com.app.bustracking.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.bustracking.presentation.model.MapRoutes
import com.app.bustracking.presentation.model.Routes

class RoutesMapViewModel : ViewModel() {

    private val _routes = MutableLiveData<List<MapRoutes>>()
    val routes: LiveData<List<MapRoutes>> get() = _routes

    fun fetchData() {
        val data: ArrayList<MapRoutes> = ArrayList()

        data.add(MapRoutes(0, "Bressuire - Bellefeuille"))
        data.add(MapRoutes(0, "Bressuire -Bocapole"))
        data.add(MapRoutes(0, "Bressuire - Carrefour"))
        data.add(MapRoutes(0, "Bressuire - Gare Sncf"))


        _routes.postValue(data)
    }

}
package com.app.bustracking.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.bustracking.presentation.model.Routes

class RoutesViewModel : ViewModel() {

    private val _routes = MutableLiveData<List<Routes>>()
    val routes: LiveData<List<Routes>> get() = _routes

    fun fetchData() {
        val data: ArrayList<Routes> = ArrayList()

        data.add(Routes(0, "104-Bressuire-Hospital CHNDS", 1, "1 Connected Vehicle"))
        data.add(Routes(0, "107-Bressuire>Chiche", 0, ""))
        data.add(Routes(0, "108-Clesse>Bressuire", 0, ""))
        data.add(Routes(0, "109-Trayes>Bressuire", 1, "1 Connected Vehicle"))
        data.add(Routes(0, "110-Moncoutant /Sevre > Bressuire", 0, ""))
        data.add(Routes(0, "117-La Foret / Sevre > Cerizay", 0, ""))

        _routes.postValue(data)
    }

}
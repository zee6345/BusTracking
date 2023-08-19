package com.app.bustracking.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.bustracking.presentation.model.SelectNetwork

class SelectRouteViewModel : ViewModel() {

    private val _network = MutableLiveData<List<SelectNetwork>>()
    val network: LiveData<List<SelectNetwork>> get() = _network

    fun fetchData() {
        val data: ArrayList<SelectNetwork> = ArrayList()
        data.add(SelectNetwork(0, "A Citadina"))
        _network.postValue(data)
    }

}
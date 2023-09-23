package com.app.bustracking.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.bustracking.data.responseModel.Route


class SharedModel : ViewModel() {

    val agentId = MutableLiveData<Int>()

    val routeData = MutableLiveData<Route>()


}
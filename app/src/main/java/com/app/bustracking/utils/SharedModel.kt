package com.app.bustracking.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedModel : ViewModel() {

    val agentId = MutableLiveData<Int>()


}
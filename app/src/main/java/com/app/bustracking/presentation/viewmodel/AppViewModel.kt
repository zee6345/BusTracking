package com.app.bustracking.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.DataState
import com.app.bustracking.domain.AppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val appUseCase: AppUseCase) : ViewModel() {

    private val _getAgenciesList = MutableLiveData<DataState<Any>?>()
    val getAgenciesList get() = _getAgenciesList

    private val _getTravelList = MutableLiveData<DataState<Any>?>()
    val getTravelList get() = _getTravelList

    private val _getBusList = MutableLiveData<DataState<Any>?>()
    val getBusList get() = _getBusList

    private val _getTravelRoutes = MutableLiveData<DataState<Any>?>()
    val getTravelRoutes get() = _getTravelRoutes


    fun getAgencies() {
        _getAgenciesList.value = DataState.Loading
        viewModelScope.launch {
            try {
                val response = appUseCase.getAgenciesList()
                if (response.isSuccessful && response.body() != null) {
                    _getAgenciesList.value = DataState.Success(response.body()!!)
                } else {
                    _getAgenciesList.value = DataState.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getAgenciesList.value = DataState.Error(e.message.toString())
            }
        }
    }

    fun getTravels(travelRequest: TravelRequest) {
        _getTravelList.value = DataState.Loading

        viewModelScope.launch {
            try {
                val response = appUseCase.getTravelList(travelRequest)
                if (response.isSuccessful && response.body() != null) {
                    _getTravelList.value = DataState.Success(response.body()!!)
                } else {
                    _getTravelList.value = DataState.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getTravelList.value = DataState.Error(e.message.toString())
            }
        }
    }

    fun getBusses(travelRequest: TravelRequest) {
        _getBusList.value = DataState.Loading

        viewModelScope.launch {
            try {
                val response = appUseCase.getBusList(travelRequest)
                if (response.isSuccessful && response.body() != null) {
                    _getBusList.value = DataState.Success(response.body()!!)
                } else {
                    _getBusList.value = DataState.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getBusList.value = DataState.Error(e.message.toString())
            }
        }
    }

    fun getTravelRouteList(travelRequest: RouteRequest) {
        _getTravelRoutes.value = DataState.Loading

        viewModelScope.launch {
            try {
                val response = appUseCase.getTravelRoutes(travelRequest)
                if (response.isSuccessful && response.body() != null) {
                    _getTravelRoutes.value = DataState.Success(response.body()!!)
                } else {
                    _getTravelRoutes.value = DataState.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getTravelRoutes.value = DataState.Error(e.message.toString())
            }
        }
    }


}
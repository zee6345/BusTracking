package com.app.bustracking.data.remotesource

import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.data.responseModel.GetBusList
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import retrofit2.Response

interface AppRepo {

    suspend fun getAgencies():Response<GetAgenciesList>

    suspend fun getTravelList(travelRequest: TravelRequest):Response<GetTravelList>

    suspend fun getBusList(travelRequest: TravelRequest):Response<GetBusList>

    suspend fun getTravelRoutes(travelRequest: RouteRequest):Response<GetTravelRoutes>

}
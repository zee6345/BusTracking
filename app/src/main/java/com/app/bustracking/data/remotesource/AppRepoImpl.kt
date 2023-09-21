package com.app.bustracking.data.remotesource

import com.app.bustracking.data.api.ApiService
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.data.responseModel.GetBusList
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import retrofit2.Response
import javax.inject.Inject

class AppRepoImpl @Inject constructor(private val apiService: ApiService) : AppRepo {

    override suspend fun getAgencies(): Response<GetAgenciesList> {
        return apiService.getAgencies()
    }

    override suspend fun getTravelList(travelRequest: TravelRequest): Response<GetTravelList> {
        return apiService.getTravelList(travelRequest)
    }

    override suspend fun getBusList(travelRequest: TravelRequest): Response<GetBusList> {
        return apiService.getBusList(travelRequest)
    }

    override suspend fun getTravelRoutes(travelRequest: RouteRequest): Response<GetTravelRoutes> {
        return apiService.getTravelRoutes(travelRequest)
    }

}
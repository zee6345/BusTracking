package com.app.bustracking.domain

import com.app.bustracking.data.remotesource.AppRepoImpl
import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.data.responseModel.GetBusList
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import retrofit2.Response
import javax.inject.Inject

class AppUseCase @Inject constructor(private val appRepoImpl: AppRepoImpl) {

    suspend fun getAgenciesList(): Response<GetAgenciesList> {
        return appRepoImpl.getAgencies()
    }

    suspend fun getTravelList(travelRequest: TravelRequest):Response<GetTravelList>{
        return appRepoImpl.getTravelList(travelRequest)
    }

    suspend fun getBusList(travelRequest: TravelRequest):Response<GetBusList>{
        return appRepoImpl.getBusList(travelRequest)
    }

    suspend fun getTravelRoutes(travelRequest: RouteRequest): Response<GetTravelRoutes> {
        return appRepoImpl.getTravelRoutes(travelRequest)
    }

}
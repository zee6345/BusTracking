package com.app.bustracking.data.api

import com.app.bustracking.data.requestModel.RouteRequest
import com.app.bustracking.data.requestModel.TravelRequest
import com.app.bustracking.data.responseModel.GetAgenciesList
import com.app.bustracking.data.responseModel.GetBusList
import com.app.bustracking.data.responseModel.GetTravelList
import com.app.bustracking.data.responseModel.GetTravelRoutes
import com.app.bustracking.data.responseModel.LoginModel
import com.app.bustracking.data.responseModel.LogoutModel
import com.app.bustracking.data.responseModel.VerifyOTPModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("logout/client")
    fun logout(): Call<LogoutModel>

    @POST("verify-otp")
    @FormUrlEncoded
    fun verifyOTP(
        @Field("mobile")mobile: String,
        @Field("otp")otp:Int
    ):Call<VerifyOTPModel>

    @POST("login/client")
    @FormUrlEncoded
    fun login(
        @Field("mobile") mobile:String
    ):Call<LoginModel>

    @POST("get_list_agency")
    suspend fun getAgencies():Response<GetAgenciesList>

    @POST("get_agency_travel_list")
    suspend fun getTravelList(
        @Body travelRequest: TravelRequest
    ):Response<GetTravelList>

    @POST("get_list_bus")
    suspend fun getBusList(
        @Body travelRequest: TravelRequest
    ):Response<GetBusList>

    @POST("get_travel_route_list")
    suspend fun getTravelRoutes(
        @Body travelRequest: RouteRequest
    ):Response<GetTravelRoutes>

}
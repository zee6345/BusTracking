package com.app.bustracking.data.api

import com.app.bustracking.data.responseModel.LoginModel
import com.app.bustracking.data.responseModel.LogoutModel
import com.app.bustracking.data.responseModel.VerifyOTPModel
import retrofit2.Call
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
}
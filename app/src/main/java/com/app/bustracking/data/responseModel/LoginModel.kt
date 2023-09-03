package com.app.bustracking.data.responseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("token")
    @Expose
    val token:String,

    @SerializedName("message")
    @Expose
    val  message:String
)

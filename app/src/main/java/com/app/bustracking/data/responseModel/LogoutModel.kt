package com.app.bustracking.data.responseModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LogoutModel (
    @SerializedName("message")
    @Expose
    val message:String
)
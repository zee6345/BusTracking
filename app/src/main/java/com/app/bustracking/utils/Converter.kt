package com.app.bustracking.utils

import com.google.gson.Gson

object Converter {

    fun toJson(obj:Any): String? {
        return Gson().toJson(obj)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return Gson().fromJson(json, classOfT)
    }

}
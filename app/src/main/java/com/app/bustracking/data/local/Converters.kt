package com.app.bustracking.data.local

import androidx.room.TypeConverter
import com.app.bustracking.data.responseModel.Stop
import com.app.bustracking.data.responseModel.Travel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun fromString(value: String): List<Travel> {
        return Gson().fromJson(value, object : TypeToken<List<Travel>>() {}.type)
    }

    @TypeConverter
    fun toString(list: List<Travel>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStopString(value: String): List<Stop> {
        return Gson().fromJson(value, object : TypeToken<List<Stop>>() {}.type)
    }

    @TypeConverter
    fun toStopString(list: List<Stop>): String {
        return Gson().toJson(list)
    }
}
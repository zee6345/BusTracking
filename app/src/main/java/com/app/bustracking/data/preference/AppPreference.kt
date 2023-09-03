package com.app.bustracking.data.preference

import android.content.Context
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs

object AppPreference {

    fun Preference(context:Context){
        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(context.packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    fun setToken(token:String){
        Prefs.putString("token",token)
    }

    fun getToken():String{
        return Prefs.getString("token","")
    }
}
package com.app.bustracking.data.preference

import android.content.Context
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs

object AppPreference {

    fun Preference(context: Context) {
        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(context.packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    fun putString(key: String, value: String) {
        Prefs.putString(key, value)
    }

    fun getString(key: String): String {
        return Prefs.getString(key) ?: ""
    }

    fun putInt(key: String, value: Int) {
        Prefs.putInt(key, value)
    }

    fun getInt(key: String): Int {
        return Prefs.getInt(key, 0)
    }

    fun putBoolean(key: String, value: Boolean) {
        Prefs.putBoolean(key, value)
    }

    fun getBoolean(key: String): Boolean {
        return Prefs.getBoolean(key, false)
    }

    fun setToken(token: String) {
        Prefs.putString("token", token)
    }

    fun getToken(): String {
        return Prefs.getString("token", "")
    }

    fun clear(){
        Prefs.clear()
    }
}
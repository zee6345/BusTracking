package com.app.bustracking.app

import android.app.Application
import android.content.Context
import com.app.bustracking.R
import com.app.bustracking.data.preference.AppPreference
import com.mapbox.maps.plugin.Plugin
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusTracking : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = this

        Plugin.Mapbox(getString(R.string.mapbox_access_token))
        AppPreference.Preference(this)

    }
}
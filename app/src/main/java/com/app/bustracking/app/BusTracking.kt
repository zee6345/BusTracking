package com.app.bustracking.app

import android.app.Application
import com.app.bustracking.R
import com.mapbox.maps.plugin.Plugin
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusTracking : Application() {

    override fun onCreate() {
        super.onCreate()

        Plugin.Mapbox(getString(R.string.mapbox_access_token))

    }
}
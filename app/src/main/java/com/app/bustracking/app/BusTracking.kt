package com.app.bustracking.app

import android.app.Application
import com.app.bustracking.R
import com.app.bustracking.data.preference.AppPreference
import com.mapbox.maps.plugin.Plugin
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusTracking : Application() {

    override fun onCreate() {
        super.onCreate()

        Plugin.Mapbox(getString(R.string.mapbox_access_token))
        AppPreference.Preference(this)

    }
}
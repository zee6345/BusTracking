package com.app.bustracking.app

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import com.app.bustracking.R
import com.mapbox.mapboxsdk.Mapbox
import com.pixplicity.easyprefs.library.Prefs

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusTracking : Application() {

    companion object {
        lateinit var context: Context
//        lateinit var appDb: AppDb
    }

    override fun onCreate() {
        super.onCreate()

        context = this

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        //db
//        appDb = AppDb.with()!!

        //prefers
        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(context.packageName)
            .setUseDefaultSharedPreference(true)
            .build()


    }
}
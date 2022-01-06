package main

import android.app.Application
import models.PlacemarkModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val placemarks = ArrayList<PlacemarkModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Placemark started")
    }
}
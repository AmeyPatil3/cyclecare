package com.cyclecare

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CycleCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

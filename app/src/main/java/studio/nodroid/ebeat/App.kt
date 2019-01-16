package studio.nodroid.ebeat

import android.app.Application
import org.koin.android.ext.android.startKoin
import studio.nodroid.ebeat.di.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}
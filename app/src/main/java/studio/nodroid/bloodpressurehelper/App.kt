package studio.nodroid.bloodpressurehelper

import android.app.Application
import org.koin.android.ext.android.startKoin
import studio.nodroid.bloodpressurehelper.di.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}
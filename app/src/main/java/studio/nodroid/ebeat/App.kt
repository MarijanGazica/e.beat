package studio.nodroid.ebeat

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import studio.nodroid.ebeat.di.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

//        val mobAdAppId = if (BuildConfig.DEBUG) {
//            "ca-app-pub-3940256099942544~3347511713"
//        } else {
//            "ca-app-pub-9002747894812216~3336404117"
//        }
    }
}
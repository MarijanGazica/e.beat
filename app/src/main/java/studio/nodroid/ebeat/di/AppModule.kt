package studio.nodroid.ebeat.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.room.*
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.sharedPrefs.SharedPrefsImpl
import studio.nodroid.ebeat.time.TimeProvider
import studio.nodroid.ebeat.ui.flow.graphs.GraphsViewModel
import studio.nodroid.ebeat.ui.flow.reading.ReadingViewModel
import studio.nodroid.ebeat.ui.flow.readingsList.ReadingsListViewModel
import studio.nodroid.ebeat.ui.flow.users.UsersViewModel
import studio.nodroid.ebeat.ui.splash.SplashViewModel

private const val DATABASE_NAME = "app_database"
private const val SHARED_PREFS_NAME = "shared_prefs"

val appModule = module {

    single<SharedPreferences> { androidApplication().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE) }
    single<SharedPrefs> { SharedPrefsImpl(get()) }

    single { Analytics(androidApplication()) }

    single { TimeProvider() }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DATABASE_NAME).build() }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().pressureDataDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PressureDataRepository> { PressureDataRepositoryImpl(get()) }

    viewModel { SplashViewModel(get(), get()) }
    viewModel { ReadingViewModel(get(), get(), get(), get()) }
    viewModel { UsersViewModel(get(), get()) }
    viewModel { GraphsViewModel(get(), get(), get()) }
    viewModel { ReadingsListViewModel(get(), get(), get()) }

}
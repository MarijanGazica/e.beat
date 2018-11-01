package studio.nodroid.bloodpressurehelper.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import studio.nodroid.bloodpressurehelper.room.*
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefsImpl
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel

private const val DATABASE_NAME = "app_database"
private const val SHARED_PREFS_NAME = "shared_prefs"

val appModule = module {

    viewModel { PressureInputViewModel(get(), get(), get()) }

    single<SharedPreferences> { androidApplication().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE) }

    single<SharedPrefs> { SharedPrefsImpl(get()) }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DATABASE_NAME).build() }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().pressureDataDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PressureDataRepository> { PressureDataRepositoryImpl(get()) }

}
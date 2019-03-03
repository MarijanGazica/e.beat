package studio.nodroid.ebeat.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.room.*
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.sharedPrefs.SharedPrefsImpl
import studio.nodroid.ebeat.ui.view.UserListViewModel
import studio.nodroid.ebeat.utils.KeyboardVisibilityProvider
import studio.nodroid.ebeat.vm.InputHistoryViewModel
import studio.nodroid.ebeat.vm.PressureInputViewModel
import studio.nodroid.ebeat.vm.UserPickerViewModel

private const val DATABASE_NAME = "app_database"
private const val SHARED_PREFS_NAME = "shared_prefs"

val appModule = module {

    viewModel { PressureInputViewModel(get(), get()) }
    viewModel { InputHistoryViewModel(get()) }
    viewModel { UserListViewModel(get(), get()) }
    viewModel { UserPickerViewModel(get(), get(), get()) }

    single<SharedPreferences> { androidApplication().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE) }
    single<SharedPrefs> { SharedPrefsImpl(get()) }

    single { Analytics(androidApplication()) }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DATABASE_NAME).build() }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().pressureDataDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PressureDataRepository> { PressureDataRepositoryImpl(get()) }

    factory { KeyboardVisibilityProvider(get()) }

}
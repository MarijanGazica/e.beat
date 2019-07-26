package studio.nodroid.ebeat.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import studio.nodroid.ebeat.ads.AdSettingsViewModel
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.room.*
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.sharedPrefs.SharedPrefsImpl
import studio.nodroid.ebeat.time.TimeProvider
import studio.nodroid.ebeat.ui.flow.reading.ReadingDetailsViewModel
import studio.nodroid.ebeat.ui.inputHistory.InputHistoryViewModel
import studio.nodroid.ebeat.ui.pressureInput.PressureInputViewModel
import studio.nodroid.ebeat.ui.readingDetails.ReadingDetailsViewModelOld
import studio.nodroid.ebeat.ui.splash.SplashViewModel
import studio.nodroid.ebeat.ui.userPicker.UserListViewModel
import studio.nodroid.ebeat.ui.userPicker.UserPickerViewModel
import studio.nodroid.ebeat.utils.KeyboardVisibilityProvider

private const val DATABASE_NAME = "app_database"
private const val SHARED_PREFS_NAME = "shared_prefs"

val appModule = module {

    viewModel { PressureInputViewModel(get(), get(), get()) }
    viewModel { InputHistoryViewModel(get(), get()) }
    viewModel { UserListViewModel(get(), get()) }
    viewModel { UserPickerViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { AdSettingsViewModel(get(), get()) }
    viewModel { ReadingDetailsViewModelOld(get()) }

    single<SharedPreferences> { androidApplication().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE) }
    single<SharedPrefs> { SharedPrefsImpl(get()) }

    single { Analytics(androidApplication()) }

    single { TimeProvider() }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DATABASE_NAME).build() }

    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().pressureDataDao() }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PressureDataRepository> { PressureDataRepositoryImpl(get()) }

    factory { KeyboardVisibilityProvider(get()) }

    viewModel { ReadingDetailsViewModel(get(), get(), get()) }

}
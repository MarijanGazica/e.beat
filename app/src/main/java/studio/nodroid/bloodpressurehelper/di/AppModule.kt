package studio.nodroid.bloodpressurehelper.di

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel

val appModule = module {
    viewModel { PressureInputViewModel() }
}
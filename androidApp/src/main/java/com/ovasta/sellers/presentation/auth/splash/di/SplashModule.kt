package com.ovasta.sellers.presentation.auth.splash.di

import com.ovasta.sellers.ui.screens.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}

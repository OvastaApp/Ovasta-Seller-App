package com.ovasta.sellers.presentation.home.di

import com.ovasta.sellers.presentation.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    // IHomeRepository is provided by sharedModule
    viewModel { HomeViewModel(get(), get()) }
}
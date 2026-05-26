package com.ovasta.sellers.presentation.profile.orderhistory.di

import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val orderHistoryModule = module {
    // IOrderHistoryRepository is provided by sharedModule
    viewModel { OrderHistoryViewModel(get()) }
}
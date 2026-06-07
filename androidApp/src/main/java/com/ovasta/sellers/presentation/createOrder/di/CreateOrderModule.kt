package com.ovasta.sellers.presentation.createOrder.di

import com.ovasta.sellers.ui.screens.createorder.CreateOrderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val createOrderModule = module {
    // ICreateOrderRepository is provided by sharedModule
    viewModel { CreateOrderViewModel(get(), get()) }
}
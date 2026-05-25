package com.ovasta.sellers.di

import com.ovasta.sellers.base.StringResourceProvider
import com.ovasta.sellers.platform.AndroidStringResourceProvider
import com.ovasta.sellers.presentation.createOrder.data.CreateOrderRemoteDataSource
import com.ovasta.sellers.presentation.createOrder.data.CreateOrderRepository
import com.ovasta.sellers.presentation.createOrder.data.ICreateOrderRemoteDataSource
import com.ovasta.sellers.presentation.createOrder.data.ICreateOrderRepository
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val createOrderModule = module {
    single<ICreateOrderRemoteDataSource> { CreateOrderRemoteDataSource(get()) }
    single<ICreateOrderRepository> { CreateOrderRepository(get()) }
    single<StringResourceProvider> { AndroidStringResourceProvider(get()) }
    viewModel { CreateOrderViewModel(get(), get(), get()) }
}

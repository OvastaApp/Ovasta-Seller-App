package com.ovasta.sellers.presentation.createOrder.di

import com.ovasta.sellers.presentation.createOrder.data.CreateOrderApi
import com.ovasta.sellers.presentation.createOrder.data.CreateOrderRemoteDataSource
import com.ovasta.sellers.presentation.createOrder.data.CreateOrderRepository
import com.ovasta.sellers.presentation.createOrder.data.ICreateOrderRemoteDataSource
import com.ovasta.sellers.presentation.createOrder.data.ICreateOrderRepository
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val createOrderModule = module {
    factory { get<Retrofit>().create(CreateOrderApi::class.java) }
    single<ICreateOrderRemoteDataSource> { CreateOrderRemoteDataSource(get()) }
    single<ICreateOrderRepository> { CreateOrderRepository(get()) }
    viewModel { CreateOrderViewModel(get()) }
}
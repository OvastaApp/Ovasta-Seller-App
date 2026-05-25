package com.ovasta.sellers.presentation.profile.orderhistory.di

import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRemoteDataSource
import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRepository
import com.ovasta.sellers.presentation.profile.orderhistory.data.OrderHistoryApi
import com.ovasta.sellers.presentation.profile.orderhistory.data.OrderHistoryRemoteDataSource
import com.ovasta.sellers.presentation.profile.orderhistory.data.OrderHistoryRepository
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val orderHistoryModule = module {
    factory { get<Retrofit>().create(OrderHistoryApi::class.java) }
    single<IOrderHistoryRemoteDataSource> { OrderHistoryRemoteDataSource(get()) }
    single<IOrderHistoryRepository> { OrderHistoryRepository(get()) }
    viewModel { OrderHistoryViewModel(get()) }
}
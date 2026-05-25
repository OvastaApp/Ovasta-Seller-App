package com.ovasta.sellers.presentation.profile.orderhistory.di

import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRemoteDataSource
import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRepository
import com.ovasta.sellers.presentation.profile.orderhistory.data.OrderHistoryRemoteDataSource
import com.ovasta.sellers.presentation.profile.orderhistory.data.OrderHistoryRepository
import com.ovasta.sellers.presentation.profile.orderhistory.presentation.OrderHistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val orderHistoryModule = module {
    single<IOrderHistoryRemoteDataSource> { OrderHistoryRemoteDataSource(get()) }
    single<IOrderHistoryRepository> { OrderHistoryRepository(get()) }
    viewModel { OrderHistoryViewModel(get()) }
}
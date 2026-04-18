package com.ovasta.sellers.presentation.orderDetails.di

import com.ovasta.sellers.presentation.orderDetails.data.IOrderDetailsRemoteDataSource
import com.ovasta.sellers.presentation.orderDetails.data.IOrderDetailsRepository
import com.ovasta.sellers.presentation.orderDetails.data.OrderDetailsRemoteDataSource
import com.ovasta.sellers.presentation.orderDetails.data.OrderDetailsRepository
import com.ovasta.sellers.presentation.orderDetails.presentation.TaskDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val taskDetailsModule = module {
    single<IOrderDetailsRemoteDataSource> { OrderDetailsRemoteDataSource(get()) }
    single<IOrderDetailsRepository> { OrderDetailsRepository(get()) }
    viewModel { TaskDetailsViewModel(get(), get()) }
}
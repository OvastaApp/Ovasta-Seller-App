package com.ovasta.sellers.presentation.home.di

import com.ovasta.sellers.presentation.home.data.HomeRemoteDataSource
import com.ovasta.sellers.presentation.home.data.HomeRepository
import com.ovasta.sellers.presentation.home.data.IHomeRemoteDataSource
import com.ovasta.sellers.presentation.home.data.IHomeRepository
import com.ovasta.sellers.presentation.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single<IHomeRemoteDataSource> { HomeRemoteDataSource(get()) }
    single<IHomeRepository> { HomeRepository(get()) }
    viewModel { HomeViewModel(get(), get()) }
}
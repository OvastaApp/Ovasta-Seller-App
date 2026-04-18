package com.ovasta.sellers.presentation.home.di

import com.ovasta.sellers.base.services.LocationManager
import com.ovasta.sellers.presentation.home.data.HomeApi
import com.ovasta.sellers.presentation.home.data.HomeRemoteDataSource
import com.ovasta.sellers.presentation.home.data.HomeRepository
import com.ovasta.sellers.presentation.home.data.IHomeRemoteDataSource
import com.ovasta.sellers.presentation.home.data.IHomeRepository
import com.ovasta.sellers.presentation.home.presentation.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule = module {
    factory { get<Retrofit>().create(HomeApi::class.java) }
    single<IHomeRemoteDataSource> { HomeRemoteDataSource(get(),get()) }
    single<IHomeRepository> { HomeRepository(get(), get()) }
    single { LocationManager(androidContext()) }
    viewModel { HomeViewModel(get(), get(), get()) }
}
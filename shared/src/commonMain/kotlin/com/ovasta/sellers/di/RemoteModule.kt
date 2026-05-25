package com.ovasta.sellers.di

import com.ovasta.sellers.data.network.AuthTokenProvider
import com.ovasta.sellers.data.network.SellerApiService
import com.ovasta.sellers.data.notification.FcmTokenRemoteDataSource
import com.ovasta.sellers.data.notification.IFcmTokenRemoteDataSource
import com.ovasta.sellers.data.network.createHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val remoteModule = module {
    single<AuthTokenProvider> { SessionAuthTokenProvider(get()) }
    single { SellerApiService(createHttpClient(get())) }
    single<IFcmTokenRemoteDataSource> { FcmTokenRemoteDataSource(get()) }
}

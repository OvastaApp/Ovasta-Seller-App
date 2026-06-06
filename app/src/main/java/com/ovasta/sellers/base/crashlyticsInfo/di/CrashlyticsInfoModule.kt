package com.ovasta.sellers.base.crashlyticsInfo.di

import com.ovasta.sellers.base.crashlyticsInfo.CrashlyticsInfoRemoteDataSource
import com.ovasta.sellers.base.crashlyticsInfo.CrashlyticsUserInfoUseCase
import com.ovasta.sellers.base.crashlyticsInfo.ICrashlyticsInfoRemoteDataSource
import org.koin.dsl.module

val crashlyticsInfoModule = module {
    single<ICrashlyticsInfoRemoteDataSource> { CrashlyticsInfoRemoteDataSource() }
    single { CrashlyticsUserInfoUseCase(get()) }
}
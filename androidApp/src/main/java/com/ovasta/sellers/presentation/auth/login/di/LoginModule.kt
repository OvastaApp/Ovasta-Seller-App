package com.ovasta.sellers.presentation.auth.login.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.presentation.auth.login.data.ILoginRemoteDataSource
import com.ovasta.sellers.presentation.auth.login.data.LoginRemoteDataSource
import com.ovasta.sellers.presentation.auth.login.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    single<ILoginRemoteDataSource> { LoginRemoteDataSource(get()) }
    // Note: ILoginRepository is now injected from shared module (sharedModule provides it)
    viewModel { LoginViewModel(get(), get(), get<DataStore<SessionPreferences>>()) }
}
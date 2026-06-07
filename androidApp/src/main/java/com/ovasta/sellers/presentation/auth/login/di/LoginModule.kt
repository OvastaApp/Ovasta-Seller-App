package com.ovasta.sellers.presentation.auth.login.di

import com.ovasta.sellers.presentation.auth.login.data.ILoginRemoteDataSource
import com.ovasta.sellers.presentation.auth.login.data.LoginRemoteDataSource
import com.ovasta.sellers.ui.screens.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    single<ILoginRemoteDataSource> { LoginRemoteDataSource(get()) }
    // Note: ILoginRepository is now injected from shared module (sharedModule provides it)
    viewModel { LoginViewModel(get(), get()) }
}
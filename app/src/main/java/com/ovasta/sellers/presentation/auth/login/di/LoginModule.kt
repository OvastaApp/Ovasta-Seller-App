package com.ovasta.sellers.presentation.auth.login.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.presentation.auth.login.data.ILoginRemoteDataSource
import com.ovasta.sellers.presentation.auth.login.data.ILoginRepository
import com.ovasta.sellers.presentation.auth.login.data.LoginApi
import com.ovasta.sellers.presentation.auth.login.data.LoginRemoteDataSource
import com.ovasta.sellers.presentation.auth.login.data.LoginRepository
import com.ovasta.sellers.presentation.auth.login.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val loginModule = module {
    factory { get<Retrofit>().create(LoginApi::class.java) }
    single<ILoginRemoteDataSource> { LoginRemoteDataSource(get()) }
    single<ILoginRepository> { LoginRepository(get()) }
    viewModel { LoginViewModel(get(), get(), get<DataStore<SessionPreferences>>()) }
}
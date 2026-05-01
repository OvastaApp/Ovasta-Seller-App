package com.ovasta.sellers.presentation.profile.profile.di

import com.ovasta.sellers.presentation.profile.profile.data.IProfileRemoteDataSource
import com.ovasta.sellers.presentation.profile.profile.data.IProfileRepository
import com.ovasta.sellers.presentation.profile.profile.data.ProfileApi
import com.ovasta.sellers.presentation.profile.profile.data.ProfileRemoteDataSource
import com.ovasta.sellers.presentation.profile.profile.data.ProfileRepository
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val profileModule = module {
    factory { get<Retrofit>().create(ProfileApi::class.java) }
    single<IProfileRemoteDataSource> { ProfileRemoteDataSource(get()) }
    single<IProfileRepository> { ProfileRepository(get()) }
    viewModel { ProfileViewModel(get(),get()) }
}
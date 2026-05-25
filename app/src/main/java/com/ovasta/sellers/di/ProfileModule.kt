package com.ovasta.sellers.di

import com.ovasta.sellers.presentation.profile.profile.data.IProfileRemoteDataSource
import com.ovasta.sellers.presentation.profile.profile.data.IProfileRepository
import com.ovasta.sellers.presentation.profile.profile.data.ProfileRemoteDataSource
import com.ovasta.sellers.presentation.profile.profile.data.ProfileRepository
import com.ovasta.sellers.presentation.profile.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single<IProfileRemoteDataSource> { ProfileRemoteDataSource(get()) }
    single<IProfileRepository> { ProfileRepository(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}

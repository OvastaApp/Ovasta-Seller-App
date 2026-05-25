package com.ovasta.sellers.presentation.profile.wallet.di

import com.ovasta.sellers.presentation.profile.wallet.data.IWalletRemoteDataSource
import com.ovasta.sellers.presentation.profile.wallet.data.IWalletRepository
import com.ovasta.sellers.presentation.profile.wallet.data.WalletRemoteDataSource
import com.ovasta.sellers.presentation.profile.wallet.data.WalletRepository
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val walletModule = module {
    single<IWalletRemoteDataSource> { WalletRemoteDataSource(get()) }
    single<IWalletRepository> { WalletRepository(get()) }
    viewModel { WalletViewModel(get(), get()) }
}
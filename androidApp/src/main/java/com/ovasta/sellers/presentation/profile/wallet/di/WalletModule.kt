package com.ovasta.sellers.presentation.profile.wallet.di

import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val walletModule = module {
    // IWalletRepository is provided by sharedModule
    viewModel { WalletViewModel(get(), get()) }
}
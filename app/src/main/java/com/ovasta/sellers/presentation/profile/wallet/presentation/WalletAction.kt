package com.ovasta.sellers.presentation.profile.wallet.presentation

sealed interface WalletAction {
    object LoadOrderHistory : WalletAction
}
package com.ovasta.sellers.presentation.profile.wallet.presentation

sealed interface WalletAction {
    object LoadWalletTransactions : WalletAction
    object LoadWithdrawRequests : WalletAction
    data class SelectTab(val index: Int) : WalletAction
    object RequestWithdraw : WalletAction
    object ConfirmWithdraw : WalletAction
    object DismissWithdrawDialog : WalletAction
    object DismissSuccessDialog : WalletAction
    object ConvertPoints : WalletAction
}
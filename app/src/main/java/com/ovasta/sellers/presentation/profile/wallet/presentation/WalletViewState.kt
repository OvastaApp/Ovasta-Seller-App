package com.ovasta.sellers.presentation.profile.wallet.presentation

import com.ovasta.sellers.presentation.profile.wallet.data.WalletTransactions
import com.ovasta.sellers.presentation.profile.wallet.data.WalletTransactionsResponse
import com.ovasta.sellers.presentation.profile.wallet.data.WithdrawRequests

data class WalletViewState(
    val walletTransactions: WalletTransactionsResponse? = null,
    val withdrawRequests: List<WithdrawRequests> = emptyList(),
    val selectedTab: Int = 0
)
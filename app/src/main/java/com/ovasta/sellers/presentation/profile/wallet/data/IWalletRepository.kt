package com.ovasta.sellers.presentation.profile.wallet.data

interface IWalletRepository {
    suspend fun getWalletTransactions(page: Int? = null): WalletTransactionsResponse

    suspend fun getWithdrawalRequests(page: Int? = null): List<WithdrawRequests>

}
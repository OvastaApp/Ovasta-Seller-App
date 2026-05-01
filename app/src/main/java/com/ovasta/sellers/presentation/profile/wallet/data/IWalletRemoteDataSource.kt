package com.ovasta.sellers.presentation.profile.wallet.data

interface IWalletRemoteDataSource {
    suspend fun getWalletTransactions(page: Int? = null): WalletTransactionsResponse

    suspend fun getWithdrawalRequests(page: Int? = null): List<WithdrawRequests>
}
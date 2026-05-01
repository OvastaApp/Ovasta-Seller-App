package com.ovasta.sellers.presentation.profile.wallet.data

class WalletRemoteDataSource(
    private val walletApi: WalletApi
) : IWalletRemoteDataSource {
    override suspend fun getWalletTransactions(page: Int?) =
        walletApi.getWalletTransactions(page = page).data

    override suspend fun getWithdrawalRequests(page: Int?) =
        walletApi.getWithdrawalRequests(page = page).data
}
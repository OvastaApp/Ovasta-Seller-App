package com.ovasta.sellers.presentation.profile.wallet.data


interface IWalletRemoteDataSource {
    suspend fun getWalletTransactions(page: Int? = null): WalletResponse

    suspend fun getWithdrawalRequests(page: Int? = null): List<WithdrawRequests>

    suspend fun redeemPoints(points: Int)

    suspend fun requestWithdraw()
}
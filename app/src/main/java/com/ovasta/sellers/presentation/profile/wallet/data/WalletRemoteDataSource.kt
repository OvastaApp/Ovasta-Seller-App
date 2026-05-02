package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.presentation.profile.wallet.data.model.RedeemPointsRequest

class WalletRemoteDataSource(
    private val walletApi: WalletApi
) : IWalletRemoteDataSource {
    override suspend fun getWalletTransactions(page: Int?) =
        walletApi.getWalletTransactions(page = page).data

    override suspend fun getWithdrawalRequests(page: Int?) =
        walletApi.getWithdrawalRequests(page = page).data

    override suspend fun redeemPoints(points: Int) =
        walletApi.redeemPoints(RedeemPointsRequest(points))

    override suspend fun requestWithdraw() =
        walletApi.requestWithdraw()
}
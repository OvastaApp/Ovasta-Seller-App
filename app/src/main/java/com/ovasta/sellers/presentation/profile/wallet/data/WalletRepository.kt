package com.ovasta.sellers.presentation.profile.wallet.data

class WalletRepository(
    private val walletRemoteDataSource: IWalletRemoteDataSource,
) : IWalletRepository {
    override suspend fun getWalletTransactions(page: Int?) =
        walletRemoteDataSource.getWalletTransactions(page = page)

    override suspend fun getWithdrawalRequests(page: Int?) =
        walletRemoteDataSource.getWithdrawalRequests(page = page)

    override suspend fun redeemPoints(points: Int) =
        walletRemoteDataSource.redeemPoints(points = points)

    override suspend fun requestWithdraw(amount: Double) =
        walletRemoteDataSource.requestWithdraw(amount = amount)
}
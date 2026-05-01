package com.ovasta.sellers.presentation.profile.wallet.data

class WalletRepository(
    private val profileRemoteDataSource: IWalletRemoteDataSource,
) : IWalletRepository {
    override suspend fun getLastOrders(page: Int?) =
        profileRemoteDataSource.getLastOrders(page = page)
}
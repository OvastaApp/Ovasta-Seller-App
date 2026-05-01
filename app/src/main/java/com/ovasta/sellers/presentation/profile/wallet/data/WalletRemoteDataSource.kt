package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

class WalletRemoteDataSource(
    private val profileApi: WalletApi
) : IWalletRemoteDataSource {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse =
        profileApi.getLastOrders(page = page).data
}
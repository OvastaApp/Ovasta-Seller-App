package com.ovasta.sellers.presentation.profile.wallet.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IWalletRepository {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse?

}
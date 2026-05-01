package com.ovasta.sellers.presentation.profile.wallet.presentation

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

data class WalletViewState(
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isRefreshing: Boolean = false,
)
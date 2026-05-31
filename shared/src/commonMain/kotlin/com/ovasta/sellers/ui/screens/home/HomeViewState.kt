package com.ovasta.sellers.ui.screens.home

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isRefreshing: Boolean? = false,
)

sealed interface HomeScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : HomeScreenActions
    data object CreateOrder : HomeScreenActions
    data class OrderClicked(val orderId: Int) : HomeScreenActions
    data object RefreshHome : HomeScreenActions
    data class CancelOrder(val orderId: Int) : HomeScreenActions
    data object NavigateToWallet : HomeScreenActions
}

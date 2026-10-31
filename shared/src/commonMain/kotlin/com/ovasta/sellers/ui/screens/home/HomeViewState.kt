package com.ovasta.sellers.ui.screens.home

import com.ovasta.sellers.domain.model.DeliveryOrdersResponse
import com.ovasta.sellers.domain.model.HomeInfo
import com.ovasta.sellers.domain.model.MilestonesProgress

data class HomeViewState(
    val homeInfo: HomeInfo? = null,
    val deliveryOrdersResponse: DeliveryOrdersResponse? = null,
    val milestonesProgress: MilestonesProgress? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isRefreshing: Boolean? = false,
)

sealed interface HomeScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : HomeScreenActions
    data object CreateOrder : HomeScreenActions
    data object QuickOrder : HomeScreenActions
    data class OrderClicked(val orderId: Int) : HomeScreenActions
    data object RefreshHome : HomeScreenActions
    data class CancelOrder(val orderId: Int) : HomeScreenActions
    data object NavigateToWallet : HomeScreenActions
}

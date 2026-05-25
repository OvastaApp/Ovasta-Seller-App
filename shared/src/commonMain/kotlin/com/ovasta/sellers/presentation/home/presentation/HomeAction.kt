package com.ovasta.sellers.presentation.home.presentation

sealed interface HomeScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : HomeScreenActions
    object CreateOrder : HomeScreenActions
    data class OrderClicked(val orderId: Int) : HomeScreenActions
    object RefreshHome : HomeScreenActions
    data class CancelOrder(val orderId: Int) : HomeScreenActions
    object NavigateToWallet : HomeScreenActions
}

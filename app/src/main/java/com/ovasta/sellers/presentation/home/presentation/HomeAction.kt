package com.ovasta.sellers.presentation.home.presentation

import android.content.Context
import com.ovasta.sellers.presentation.home.data.model.HomeTask


sealed interface HomeScreenActions {
    data class ChangeLogoutDialogStatus(val isVisible: Boolean) : HomeScreenActions
    object OnLogoutClicked : HomeScreenActions
    object CreateOrder : HomeScreenActions
    data class OrderClicked(val orderId: Int) : HomeScreenActions
    data class CallCourier(val phone: String) : HomeScreenActions
    object RefreshHome : HomeScreenActions

    data class CancelOrder(val orderId: Int) : HomeScreenActions

}
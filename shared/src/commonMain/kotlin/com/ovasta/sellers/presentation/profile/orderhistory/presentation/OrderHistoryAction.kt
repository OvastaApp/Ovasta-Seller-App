package com.ovasta.sellers.presentation.profile.orderhistory.presentation

sealed interface OrderHistoryAction {
    object LoadOrderHistory : OrderHistoryAction
}

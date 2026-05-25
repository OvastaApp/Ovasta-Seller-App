package com.ovasta.sellers.presentation.nav

sealed class AppRoute {
    data object Splash : AppRoute()
    data object Login : AppRoute()
    data object Home : AppRoute()
    data class CreateOrder(val id: Long = 0) : AppRoute()
    data object Profile : AppRoute()
    data object LastOrders : AppRoute()
    data object Wallet : AppRoute()
}

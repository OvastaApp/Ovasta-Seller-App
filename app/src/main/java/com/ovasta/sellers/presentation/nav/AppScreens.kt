package com.ovasta.sellers.presentation.nav

data object Splash
data object Login
data object Home
data class CreateOrder(val id: Long = System.currentTimeMillis())

data object Profile
data object LastOrders
data object Wallet
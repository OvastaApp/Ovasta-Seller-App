package com.ovasta.sellers.presentation.nav

data object Splash
data object Login
data object Home
data object Profile
data object LastOrders
data class CreateOrder(val id: Long = System.currentTimeMillis())

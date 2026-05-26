package com.ovasta.sellers.presentation.nav

import androidx.annotation.Keep

@Keep
data object Splash

@Keep
data object Login

@Keep
data object Home

@Keep
data class CreateOrder(val id: Long = System.currentTimeMillis())

@Keep
data object Profile

@Keep
data object LastOrders

@Keep
data object Wallet
package com.ovasta.sellers.presentation.profile.presentation

import com.ovasta.sellers.data.User
import com.ovasta.sellers.presentation.home.data.model.HomeInfo

data class ProfileViewState(
    val homeInfo: HomeInfo? = null,
    val userInfo: User? = null,
    val isRefreshing: Boolean = false,
    val walletBalance: Double = 0.0,
    val points: Double = 0.0,
    val note: String = ""
)
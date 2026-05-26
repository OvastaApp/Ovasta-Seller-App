package com.ovasta.sellers.presentation.profile.profile.presentation

import com.ovasta.sellers.domain.model.User
import com.ovasta.sellers.domain.model.HomeInfo

data class ProfileViewState(
    val homeInfo: HomeInfo? = null,
    val userInfo: User? = null,
    val walletBalance: Double = 0.0,
    val points: Double = 0.0,
)
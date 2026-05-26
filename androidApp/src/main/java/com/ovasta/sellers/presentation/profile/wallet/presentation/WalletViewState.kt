package com.ovasta.sellers.presentation.profile.wallet.presentation

import com.ovasta.sellers.domain.model.WalletResponse
import com.ovasta.sellers.domain.model.WithdrawRequests

data class WalletViewState(
    val wallet: WalletResponse? = null,
    val withdrawRequests: List<WithdrawRequests> = emptyList(),
    val selectedTab: Int = 0,
    val pointsToRedeem: Int = 0,
    val showWithdrawConfirmDialog: Boolean = false,
    val showWithdrawSuccessDialog: Boolean = false,
    val minimumRedeemPoints: Double = 0.0,
    val showRedeemBottomSheet: Boolean = false,
    val redeemPointsInput: String = "",
    val redeemPointsError: String? = null,
    val toastMessage: String? = null,
)
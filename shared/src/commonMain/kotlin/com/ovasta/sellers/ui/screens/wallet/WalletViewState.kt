package com.ovasta.sellers.ui.screens.wallet

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

sealed interface WalletAction {
    data object LoadWalletTransactions : WalletAction
    data object LoadWithdrawRequests : WalletAction
    data class SelectTab(val index: Int) : WalletAction
    data object RequestWithdraw : WalletAction
    data object ConfirmWithdraw : WalletAction
    data object DismissWithdrawDialog : WalletAction
    data object DismissSuccessDialog : WalletAction
    data object ConvertPoints : WalletAction
    data object DismissRedeemBottomSheet : WalletAction
    data class UpdateRedeemPoints(val points: String) : WalletAction
    data object ConfirmRedeemPoints : WalletAction
    data object DismissToast : WalletAction
}

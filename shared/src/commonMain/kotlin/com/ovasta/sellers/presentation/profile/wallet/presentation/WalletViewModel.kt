package com.ovasta.sellers.presentation.profile.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.StringIds
import com.ovasta.sellers.base.ext.ToastEvent
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.profile.wallet.data.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel(
    val walletRepository: IWalletRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(WalletViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: WalletAction) {
        when (action) {
            WalletAction.LoadWalletTransactions -> getWalletTransactions()
            WalletAction.LoadWithdrawRequests -> getWithdrawRequests()
            is WalletAction.SelectTab -> {
                updateViewState { it.copy(selectedTab = action.index) }
                if (action.index == 0) getWalletTransactions()
                else getWithdrawRequests()
            }
            is WalletAction.RequestWithdraw -> {
                val balance = viewState.value.wallet?.walletBalance ?: 0.0
                if (balance <= 0.0) {
                    emitToastEvent(
                        ToastEvent.ResourceToastEvent(StringIds.noWalletBalanceToWithdraw)
                    )
                } else {
                    updateViewState { it.copy(showWithdrawConfirmDialog = true) }
                }
            }
            is WalletAction.DismissWithdrawDialog -> {
                updateViewState { it.copy(showWithdrawConfirmDialog = false) }
            }
            is WalletAction.ConfirmWithdraw -> {
                updateViewState { it.copy(showWithdrawConfirmDialog = false) }
                requestWithdraw()
            }
            is WalletAction.DismissSuccessDialog -> {
                updateViewState { it.copy(showWithdrawSuccessDialog = false) }
            }
            WalletAction.ConvertPoints -> {
                getMinRedeemPoints {
                    val availablePoints = viewState.value.wallet?.points ?: 0.0
                    val min = viewState.value.minimumRedeemPoints
                    if (availablePoints < min) {
                        emitToastEvent(
                            ToastEvent.ResourceToastEvent(StringIds.miniRedeemMessage, listOf(min.toString()))
                        )
                    } else {
                        updateViewState {
                            it.copy(
                                showRedeemBottomSheet = true,
                                redeemPointsInput = "",
                                redeemPointsError = null
                            )
                        }
                    }
                }
            }
            WalletAction.DismissRedeemBottomSheet -> {
                updateViewState {
                    it.copy(
                        showRedeemBottomSheet = false,
                        redeemPointsInput = "",
                        redeemPointsError = null
                    )
                }
            }
            is WalletAction.UpdateRedeemPoints -> {
                updateViewState {
                    it.copy(redeemPointsInput = action.points, redeemPointsError = null)
                }
            }
            WalletAction.ConfirmRedeemPoints -> validateAndRedeemPoints()
            WalletAction.DismissToast -> {
                updateViewState { it.copy(toastMessage = null) }
            }
        }
    }

    private fun requestWithdraw() {
        val amount = viewState.value.wallet?.walletBalance ?: return
        viewModelScope.launch {
            runCatching {
                walletRepository.requestWithdraw(amount)
            }.onSuccess {
                updateViewState { it.copy(showWithdrawSuccessDialog = true, selectedTab = 1) }
                emitToastEvent(ToastEvent.ResourceToastEvent(StringIds.requestSubmittedSuccessfully))
                getWalletTransactions()
                getWithdrawRequests()
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun getWalletTransactions() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                walletRepository.getWalletTransactions()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState { it.copy(wallet = response) }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun getWithdrawRequests() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                walletRepository.getWithdrawalRequests()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState { it.copy(withdrawRequests = response) }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    private fun validateAndRedeemPoints() {
        val input = viewState.value.redeemPointsInput.toIntOrNull() ?: 0
        if (input <= 0) return
        updateViewState { it.copy(pointsToRedeem = input, showRedeemBottomSheet = false) }
        redeemPoints()
    }

    private fun getMinRedeemPoints(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                settingsRepository.getHomeInfo()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState {
                    it.copy(minimumRedeemPoints = response?.minRedeemPoints ?: 0.0)
                }
                onSuccess()
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun redeemPoints() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                walletRepository.redeemPoints(points = viewState.value.pointsToRedeem)
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(ToastEvent.ResourceToastEvent(StringIds.requestSubmittedSuccessfully))
                getWalletTransactions()
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun updateViewState(update: (WalletViewState) -> WalletViewState) {
        _viewState.update(update)
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}

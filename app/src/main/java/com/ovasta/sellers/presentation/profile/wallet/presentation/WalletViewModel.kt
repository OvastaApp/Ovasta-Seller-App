package com.ovasta.sellers.presentation.profile.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.presentation.profile.wallet.data.IWalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WalletViewModel(
    val walletRepository: IWalletRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(WalletViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: WalletAction) {
        when (action) {
            WalletAction.LoadWalletTransactions -> {
                getWalletTransactions()
            }

            WalletAction.LoadWithdrawRequests -> {
                getWithdrawRequests()
            }

            is WalletAction.SelectTab -> {
                updateViewState { it.copy(selectedTab = action.index) }
                if (action.index == 0) getWalletTransactions()
                else getWithdrawRequests()
            }

            is WalletAction.RequestWithdraw -> {
                updateViewState { it.copy(showWithdrawConfirmDialog = true) }
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
                onScreenAction(WalletAction.LoadWalletTransactions)
                onScreenAction(WalletAction.LoadWithdrawRequests)
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
                updateViewState {
                    it.copy(
                        wallet = response
                    )
                }
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
                updateViewState {
                    it.copy(
                        withdrawRequests = response
                    )
                }
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
            }.onSuccess { response ->
                setComposeUILoading(false)
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
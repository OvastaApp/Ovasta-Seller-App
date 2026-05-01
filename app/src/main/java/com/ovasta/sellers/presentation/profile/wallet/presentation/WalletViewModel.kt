package com.ovasta.sellers.presentation.profile.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRepository
import com.ovasta.sellers.presentation.profile.wallet.data.IWalletRepository
import com.ovasta.sellers.presentation.profile.wallet.data.WalletRepository
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

            WalletAction.RequestWithdraw -> {
                // TODO: Navigate to withdraw request screen
            }

            WalletAction.ConvertPoints -> {
                // TODO: Call convert points API
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
                        walletTransactions = response
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

    fun updateViewState(update: (WalletViewState) -> WalletViewState) {
        _viewState.update(update)
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}
package com.ovasta.sellers.ui.screens.wallet

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.IWalletRepository
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.no_wallet_balance_to_withdraw
import com.ovasta.sellers.shared.resources.request_submitted_successfully
import com.ovasta.sellers.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class WalletViewModel(
    private val walletRepository: IWalletRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(WalletViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: WalletAction) {
        when (action) {
            WalletAction.LoadWalletTransactions -> getWalletTransactions()
            WalletAction.LoadWithdrawRequests -> getWithdrawRequests()
            is WalletAction.SelectTab -> {
                _viewState.update { it.copy(selectedTab = action.index) }
                if (action.index == 0) getWalletTransactions() else getWithdrawRequests()
            }
            WalletAction.RequestWithdraw -> {
                val balance = _viewState.value.wallet?.walletBalance ?: 0.0
                if (balance <= 0.0) {
                    viewModelScope.launch {
                        emitMessage(getString(Res.string.no_wallet_balance_to_withdraw))
                    }
                } else {
                    _viewState.update { it.copy(showWithdrawConfirmDialog = true) }
                }
            }
            WalletAction.ConfirmWithdraw -> {
                _viewState.update { it.copy(showWithdrawConfirmDialog = false) }
                requestWithdraw()
            }
            WalletAction.DismissWithdrawDialog ->
                _viewState.update { it.copy(showWithdrawConfirmDialog = false) }
            WalletAction.DismissSuccessDialog ->
                _viewState.update { it.copy(showWithdrawSuccessDialog = false) }
            WalletAction.DismissToast ->
                _viewState.update { it.copy(toastMessage = null) }
        }
    }

    fun getWalletTransactions() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { walletRepository.getWalletTransactions() }
                .onSuccess { response ->
                    setLoading(false)
                    _viewState.update { it.copy(wallet = response) }
                }
                .onFailure { setLoading(false); handleError(it) }
        }
    }

    fun getWithdrawRequests() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { walletRepository.getWithdrawalRequests() }
                .onSuccess { response ->
                    setLoading(false)
                    _viewState.update { it.copy(withdrawRequests = response) }
                }
                .onFailure { setLoading(false); handleError(it) }
        }
    }

    private fun requestWithdraw() {
        val amount = _viewState.value.wallet?.walletBalance ?: return
        viewModelScope.launch {
            setLoading(true)
            runCatching { walletRepository.requestWithdraw(amount) }
                .onSuccess {
                    viewModelScope.launch {
                        emitMessage(getString(Res.string.request_submitted_successfully))
                    }
                    setLoading(false)
                    _viewState.update { it.copy(showWithdrawSuccessDialog = true, selectedTab = 1) }
                    getWalletTransactions()
                    getWithdrawRequests()
                }
                .onFailure { setLoading(false); handleError(it) }
        }
    }
}

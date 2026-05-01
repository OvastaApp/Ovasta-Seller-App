package com.ovasta.sellers.presentation.profile.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.presentation.profile.orderhistory.data.IOrderHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WalletViewModel(
    val profileRepository: IOrderHistoryRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(WalletViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: WalletAction) {
        when (action) {
            WalletAction.LoadOrderHistory -> {
                getLastOrders()
            }
        }
    }

    fun getLastOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                profileRepository.getLastOrders()
            }.onSuccess { response ->
                setComposeUILoading(false)
                updateViewState {
                    it.copy(
                        deliveryOrdersResponse = response
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
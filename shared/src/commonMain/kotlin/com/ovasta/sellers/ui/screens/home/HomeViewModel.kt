package com.ovasta.sellers.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.IHomeRepository
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.mini_redeem_message
import com.ovasta.sellers.shared.resources.order_cancelled_successfully
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.CreateOrder
import com.ovasta.sellers.ui.screens.Wallet
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class HomeViewModel(
    private val homeRepository: IHomeRepository,
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData(page: Int? = null, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _viewState.update { it.copy(isRefreshing = true) }
            } else {
                setLoading(true)
            }

            val pointsDeferred = async { runCatching { homeRepository.getHomeInfo() } }
            val ordersDeferred = async { runCatching { homeRepository.getCurrentOrders(page) } }

            val pointsResult = pointsDeferred.await()
            val ordersResult = ordersDeferred.await()

            setLoading(false)
            _viewState.update { it.copy(isRefreshing = false) }

            pointsResult.onSuccess { homeResponse ->
                _viewState.update { it.copy(homeInfo = homeResponse) }
                settingsRepository.saveHomeData(homeResponse)
            }.onFailure { handleError(it) }

            ordersResult.onSuccess { ordersResponse ->
                _viewState.update { it.copy(deliveryOrdersResponse = ordersResponse) }
            }.onFailure { handleError(it) }
        }
    }

    fun onScreenAction(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.ChangeLogoutDialogStatus ->
                _viewState.update { it.copy(isLogoutDialogVisible = action.isVisible) }

            is HomeScreenActions.CreateOrder ->
                emitScreenDirection(ScreenDirection.Push(CreateOrder()))

            is HomeScreenActions.OrderClicked -> { /* TODO */
            }

            is HomeScreenActions.RefreshHome -> loadHomeData(isRefresh = true)
            is HomeScreenActions.CancelOrder -> cancelOrder(action.orderId)
            is HomeScreenActions.NavigateToWallet ->
                emitScreenDirection(ScreenDirection.Push(Wallet))
        }
    }

    private fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            setLoading(true)
            runCatching { homeRepository.cancelOrder(orderId) }
                .onSuccess {
                    setLoading(false)
                    emitMessage(getString(Res.string.order_cancelled_successfully))
                    loadHomeData(isRefresh = true)
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }
}

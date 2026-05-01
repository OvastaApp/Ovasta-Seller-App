package com.ovasta.sellers.presentation.home.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.presentation.home.data.IHomeRepository
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.nav.CreateOrder
import com.ovasta.sellers.presentation.nav.Login
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    val homeRepository: IHomeRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData(page: Int? = null, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                updateViewState { it.copy(isRefreshing = true) }
            } else {
                setComposeUILoading(true)
            }
            val pointsDeferred = async {
                kotlin.runCatching { homeRepository.getHomeInfo() }
            }
            val ordersDeferred = async {
                runCatching { homeRepository.getCurrentOrders(page) }
            }
            val pointsResult = pointsDeferred.await()
            val ordersResult = ordersDeferred.await()

            setComposeUILoading(false)
            updateViewState { it.copy(isRefreshing = false) }

            pointsResult.onSuccess { homeResponse ->
                updateViewState { it.copy(homeInfo = homeResponse) }
                settingsRepository.saveHomeData(homeResponse)
            }.onFailure { updateViewStateWithFail(it) }

            ordersResult.onSuccess { ordersResponse ->
                updateViewState { it.copy(deliveryOrdersResponse = ordersResponse) }
            }.onFailure { updateViewStateWithFail(it) }
        }
    }

    fun onScreenAction(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.ChangeLogoutDialogStatus ->
                updateViewState { it.copy(isLogoutDialogVisible = action.isVisible) }

            is HomeScreenActions.CreateOrder ->
                emitScreenDirectionEvent(ScreenDirection.Push(CreateOrder()))

            is HomeScreenActions.OrderClicked -> {
                // TODO: navigate to order details
            }

            is HomeScreenActions.RefreshHome -> loadHomeData(isRefresh = true)

            is HomeScreenActions.CancelOrder -> cancelOrder(action.orderId)

            else -> Unit
        }
    }

    fun updateViewState(update: (HomeViewState) -> HomeViewState) {
        _viewState.update(update)
    }

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.cancelOrder(orderId)
            }.onSuccess {
                setComposeUILoading(false)
                loadHomeData(isRefresh = true)
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }


    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}
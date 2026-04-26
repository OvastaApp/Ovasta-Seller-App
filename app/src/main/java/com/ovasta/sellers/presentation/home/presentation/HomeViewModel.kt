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

    fun loadHomeData() {
        viewModelScope.launch {
            setComposeUILoading(true)
            val pointsDeferred = async {
                kotlin.runCatching { homeRepository.getHomeInfo() }
            }
            val ordersDeferred = async {
                kotlin.runCatching { homeRepository.getMyOrders() }
            }
            val pointsResult = pointsDeferred.await()
            val ordersResult = ordersDeferred.await()

            setComposeUILoading(false)

            pointsResult.onSuccess { points ->
                updateViewState { it.copy(homeInfo = points) }
            }.onFailure { updateViewStateWithFail(it) }

            ordersResult.onSuccess { orders ->
                updateViewState { it.copy(myOrders = orders) }
            }.onFailure { updateViewStateWithFail(it) }
        }
    }

    fun onScreenAction(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.ChangeLogoutDialogStatus ->
                updateViewState { it.copy(isLogoutDialogVisible = action.isVisible) }
            is HomeScreenActions.OnLogoutClicked -> logout()
            is HomeScreenActions.CreateOrder ->
                emitScreenDirectionEvent(ScreenDirection.Push(CreateOrder))
            is HomeScreenActions.OrderClicked -> {
                // TODO: navigate to order details
            }
            else -> Unit
        }
    }

    fun updateViewState(update: (HomeViewState) -> HomeViewState) {
        _viewState.update(update)
    }

    fun logout() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                settingsRepository.logout()
            }.onSuccess {
                setComposeUILoading(false)
                settingsRepository.clearUserData()
                emitScreenDirectionEvent(ScreenDirection.Replace(Login))
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
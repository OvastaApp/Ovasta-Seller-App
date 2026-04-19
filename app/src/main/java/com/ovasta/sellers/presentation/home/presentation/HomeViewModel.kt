package com.ovasta.sellers.presentation.home.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.presentation.home.data.IHomeRepository
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.nav.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    val homeRepository: IHomeRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    fun getHomeInfo() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.getHomeInfo()
            }.onSuccess { pointsResponse ->
                setComposeUILoading(false)
                updateViewState { state ->
                    state.copy(pointsInfo = pointsResponse)
                }
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun getMyOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                homeRepository.getMyOrders()
            }.onSuccess { ordersResponse ->
                setComposeUILoading(false)
                updateViewState { state ->
                    state.copy(myOrders = ordersResponse)
                }
            }.onFailure {
                updateViewStateWithFail(it)
            }
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
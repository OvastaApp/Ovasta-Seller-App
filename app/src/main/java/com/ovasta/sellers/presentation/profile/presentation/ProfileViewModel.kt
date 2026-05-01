package com.ovasta.sellers.presentation.profile.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import kotlinx.coroutines.launch
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.nav.LastOrders
import com.ovasta.sellers.presentation.nav.Login
import com.ovasta.sellers.presentation.profile.data.IProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    val profileRepository: IProfileRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(ProfileViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadHomeData()
    }

    fun onScreenAction(action: ProfileScreenActions) {
        when (action) {
            is ProfileScreenActions.ChangeLogoutDialogStatus -> {}
            is ProfileScreenActions.OnWalletClicked -> {
                // TODO: Navigate to wallet details
            }

            is ProfileScreenActions.OnLogout -> {
                logout()
            }

            is ProfileScreenActions.OnNoteChanged -> {
                _viewState.update { it.copy(note = action.note) }
            }

            ProfileScreenActions.RefreshOrders -> TODO()
            ProfileScreenActions.OnLastOrdersClicked -> {
                navLastOrders()
            }
        }
    }

    fun navLastOrders() {
        emitScreenDirectionEvent(ScreenDirection.Push(LastOrders))
    }

    fun getLastOrders() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
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

    fun loadHomeData() {
        viewModelScope.launch {
            setComposeUILoading(true)
            val homeInfoDeferred = async {
                kotlin.runCatching { settingsRepository.getHomeInfo() }
            }
            val userInfoDeferred = async {
                kotlin.runCatching { settingsRepository.getUseData() }
            }
            val homeResult = homeInfoDeferred.await()
            val profileResult = userInfoDeferred.await()

            setComposeUILoading(false)
            updateViewState { it.copy(isRefreshing = false) }

            homeResult.onSuccess { homeResponse ->
                updateViewState {
                    it.copy(
                        homeInfo = homeResponse,
                        walletBalance = homeResponse?.walletBalance ?: 0.0,
                        points = homeResponse?.pointsCount ?: 0.0
                    )
                }
            }.onFailure { updateViewStateWithFail(it) }

            profileResult.onSuccess { profileResponse ->
                updateViewState { it.copy(userInfo = profileResponse) }
            }.onFailure { updateViewStateWithFail(it) }
        }
    }

    fun updateViewState(update: (ProfileViewState) -> ProfileViewState) {
        _viewState.update(update)
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}
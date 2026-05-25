package com.ovasta.sellers.presentation.profile.profile.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.profile.profile.data.IProfileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.ovasta.sellers.presentation.nav.AppRoute

class ProfileViewModel(
    val profileRepository: IProfileRepository,
    val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(ProfileViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadProfileData()
    }

    fun onScreenAction(action: ProfileScreenActions) {
        when (action) {
            is ProfileScreenActions.ChangeLogoutDialogStatus -> {}
            is ProfileScreenActions.OnWalletClicked -> navWallet()
            is ProfileScreenActions.OnLogout -> logout()
            ProfileScreenActions.OnOrderHistoryTabClicked -> navLastOrders()
        }
    }

    fun navLastOrders() {
        emitScreenDirectionEvent(ScreenDirection.Push(AppRoute.LastOrders))
    }

    fun navWallet() {
        emitScreenDirectionEvent(ScreenDirection.Push(AppRoute.Wallet))
    }

    fun logout() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                settingsRepository.logout()
            }.onSuccess {
                setComposeUILoading(false)
                settingsRepository.clearUserData()
                emitScreenDirectionEvent(ScreenDirection.Replace(AppRoute.Login))
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun loadProfileData() {
        viewModelScope.launch {
            setComposeUILoading(true)
            val homeInfoDeferred = async {
                runCatching { settingsRepository.getHomeInfo() }
            }
            val userInfoDeferred = async {
                runCatching { settingsRepository.getUseData() }
            }
            val homeResult = homeInfoDeferred.await()
            val profileResult = userInfoDeferred.await()

            setComposeUILoading(false)

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

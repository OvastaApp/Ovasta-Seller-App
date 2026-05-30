package com.ovasta.sellers.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.LastOrders
import com.ovasta.sellers.ui.screens.Login
import com.ovasta.sellers.ui.screens.Wallet
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(ProfileViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadProfileData()
    }

    fun onScreenAction(action: ProfileScreenActions) {
        when (action) {
            is ProfileScreenActions.ChangeLogoutDialogStatus -> {}
            is ProfileScreenActions.OnWalletClicked ->
                emitScreenDirection(ScreenDirection.Push(Wallet))
            is ProfileScreenActions.OnOrderHistoryTabClicked ->
                emitScreenDirection(ScreenDirection.Push(LastOrders))
            is ProfileScreenActions.OnLogout -> logout()
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            setLoading(true)
            val homeDeferred = async { runCatching { settingsRepository.getHomeInfo() } }
            val userDeferred = async { runCatching { settingsRepository.getUserData() } }

            val homeResult = homeDeferred.await()
            val userResult = userDeferred.await()
            setLoading(false)

            homeResult.onSuccess { homeResponse ->
                _viewState.update {
                    it.copy(
                        homeInfo = homeResponse,
                        walletBalance = homeResponse?.walletBalance ?: 0.0,
                        points = homeResponse?.pointsCount ?: 0.0
                    )
                }
            }.onFailure { handleError(it) }

            userResult.onSuccess { user ->
                _viewState.update { it.copy(userInfo = user) }
            }.onFailure { handleError(it) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { settingsRepository.logout() }
                .onSuccess {
                    setLoading(false)
                    settingsRepository.clearUserData()
                    emitScreenDirection(ScreenDirection.Replace(Login))
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }
}

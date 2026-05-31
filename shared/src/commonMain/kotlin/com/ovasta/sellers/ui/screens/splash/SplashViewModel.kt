package com.ovasta.sellers.ui.screens.splash

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.Home
import com.ovasta.sellers.ui.screens.Login
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            ensureDeviceId()
            navNextScreen()
        }
    }

    private suspend fun ensureDeviceId() {
        val savedId = settingsRepository.getDeviceId()
        if (savedId.isEmpty()) {
            // Device ID will be provided by platform-specific DeviceInfoProvider
            // which is already wired into SettingsRepository
        }
    }

    private suspend fun navNextScreen() {
        delay(500)
        val loggedIn = settingsRepository.getUserData()?.deliveryId != null
        if (loggedIn) {
            emitScreenDirection(ScreenDirection.Replace(Home))
        } else {
            emitScreenDirection(ScreenDirection.Replace(Login))
        }
    }
}

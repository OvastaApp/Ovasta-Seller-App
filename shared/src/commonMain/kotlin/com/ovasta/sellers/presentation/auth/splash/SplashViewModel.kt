package com.ovasta.sellers.presentation.auth.splash

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.ovasta.sellers.presentation.nav.AppRoute

class SplashViewModel(
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    init {
        navNextScreen()
    }

    private fun navNextScreen() {
        viewModelScope.launch {
            delay(500)
            val loggedIn = settingsRepository.getUseData()?.deliveryId != null
            if (loggedIn) {
                emitScreenDirectionEvent(ScreenDirection.Replace(AppRoute.Home))
            } else {
                emitScreenDirectionEvent(ScreenDirection.Replace(AppRoute.Login))
            }
        }
    }
}

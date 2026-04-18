package com.ovasta.sellers.presentation.auth.splash

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.nav.Home
import com.ovasta.sellers.presentation.nav.Login
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val settingsRepository: ISettingsRepository) : BaseViewModel() {

    init {
        navNextScreen()
    }

    private fun navNextScreen() {
        viewModelScope.launch {
            delay(500)

            val loggedIn = settingsRepository.getUseData()?.deliveryId != null

            if (loggedIn) {
                emitScreenDirectionEvent(ScreenDirection.Replace(Home))
            } else {
                emitScreenDirectionEvent(ScreenDirection.Replace(Login))
            }
        }
    }
}
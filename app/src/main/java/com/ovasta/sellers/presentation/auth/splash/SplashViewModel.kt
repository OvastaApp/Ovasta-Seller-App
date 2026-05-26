package com.ovasta.sellers.presentation.auth.splash

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.nav.Home
import com.ovasta.sellers.presentation.nav.Login
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val settingsRepository: ISettingsRepository,
    private val context: Context
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
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            settingsRepository.saveDeviceId(androidId)
        }
    }

    private suspend fun navNextScreen() {
        delay(500)

        val loggedIn = settingsRepository.getUseData()?.deliveryId != null

        if (loggedIn) {
            emitScreenDirectionEvent(ScreenDirection.Replace(Home))
        } else {
            emitScreenDirectionEvent(ScreenDirection.Replace(Login))
        }
    }
}

package com.ovasta.sellers.ui.screens.splash

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.platform.setCrashlyticsUser
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.Home
import com.ovasta.sellers.ui.screens.Login
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    // StateFlow so the destination is always replayed to new collectors.
    // This survives activity recreation (ViewModel is retained on config change).
    private val _destination = MutableStateFlow<ScreenDirection?>(null)
    val destination = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getDeviceId()
            delay(500)
            val user = settingsRepository.getUserData()
            val loggedIn = user != null
            if (user != null) {
                // Restore seller identity on crash reports after relaunch.
                setCrashlyticsUser(user.id.toString(), user.name.orEmpty(), user.mobile.orEmpty())
            }
            _destination.value = if (loggedIn) ScreenDirection.Replace(Home)
                                  else ScreenDirection.Replace(Login)
        }
    }
}

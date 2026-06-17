package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Global handler for unauthorized (401) responses.
 * Emits events when the user needs to be logged out and redirected to the login screen.
 */
class UnauthorizedHandler(
    private val settingsRepository: ISettingsRepository
) {
    private val _unauthorizedEvent = MutableSharedFlow<Unit>(replay = 0)
    val unauthorizedEvent: SharedFlow<Unit> = _unauthorizedEvent.asSharedFlow()

    suspend fun onUnauthorized() {
        // Clear user data
        try {
            settingsRepository.clearUserData()
        } catch (e: Exception) {
            // Ignore errors during logout
        }
        // Emit event to navigate to login
        _unauthorizedEvent.emit(Unit)
    }
}

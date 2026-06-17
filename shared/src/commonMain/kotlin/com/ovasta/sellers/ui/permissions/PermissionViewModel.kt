package com.ovasta.sellers.ui.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.data.platform.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    init {
        checkPermissionStatus()
    }

    private fun checkPermissionStatus() {
        viewModelScope.launch {
            val isGranted = permissionManager.isNotificationPermissionGranted()
            _permissionState.update {
                it.copy(
                    isGranted = isGranted,
                    shouldShowDialog = !isGranted && !it.userDismissed
                )
            }
        }
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            _permissionState.update { it.copy(isRequesting = true) }

            val granted = permissionManager.requestNotificationPermission()

            _permissionState.update {
                PermissionState(
                    isRequesting = false,
                    isGranted = granted,
                    hasRequested = true,
                    shouldShowDialog = false,
                    shouldShowDeniedDialog = !granted
                )
            }
        }
    }

    fun dismissPermissionDialog() {
        _permissionState.update {
            it.copy(
                shouldShowDialog = false,
                userDismissed = true
            )
        }
    }

    fun dismissDeniedDialog() {
        _permissionState.update { it.copy(shouldShowDeniedDialog = false) }
    }

    fun openSettings() {
        permissionManager.openAppSettings()
        dismissDeniedDialog()
    }
}

data class PermissionState(
    val isRequesting: Boolean = false,
    val isGranted: Boolean = false,
    val hasRequested: Boolean = false,
    val shouldShowDialog: Boolean = false,
    val shouldShowDeniedDialog: Boolean = false,
    val userDismissed: Boolean = false
)

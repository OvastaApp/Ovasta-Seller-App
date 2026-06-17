package com.ovasta.sellers.data.platform

expect class PermissionManager {
    suspend fun requestNotificationPermission(): Boolean
    suspend fun isNotificationPermissionGranted(): Boolean
    fun openAppSettings()
}

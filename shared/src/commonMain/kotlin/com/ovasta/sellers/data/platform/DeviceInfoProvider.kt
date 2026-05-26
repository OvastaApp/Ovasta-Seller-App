package com.ovasta.sellers.data.platform

/**
 * Platform-specific device info provider.
 * Android: UUID + Settings.Secure.ANDROID_ID
 * iOS: UUID + Keychain persistence
 */
expect class DeviceInfoProvider {
    fun getDeviceId(): String
    fun generateDeviceId(): String
}

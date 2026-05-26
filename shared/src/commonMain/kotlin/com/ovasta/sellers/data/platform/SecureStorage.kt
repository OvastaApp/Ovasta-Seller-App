package com.ovasta.sellers.data.platform

/**
 * Platform-specific storage for secure key-value data (tokens, device ID).
 * Android: EncryptedSharedPreferences
 * iOS: Keychain
 */
expect class SecureStorage {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
    fun clear()
}

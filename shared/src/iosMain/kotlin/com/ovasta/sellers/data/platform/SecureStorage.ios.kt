package com.ovasta.sellers.data.platform

import platform.Foundation.NSUserDefaults

/**
 * iOS SecureStorage implementation using NSUserDefaults.
 * TODO: Replace with iOS Keychain for production security.
 * For now, using NSUserDefaults as a stub to enable compilation.
 */
actual class SecureStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val prefix = "secure_"
    
    actual fun getString(key: String): String? {
        return userDefaults.stringForKey(prefix + key)
    }
    
    actual fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = prefix + key)
        userDefaults.synchronize()
    }
    
    actual fun remove(key: String) {
        userDefaults.removeObjectForKey(prefix + key)
        userDefaults.synchronize()
    }
    
    actual fun clear() {
        // Clear all keys with our prefix
        val allKeys = userDefaults.dictionaryRepresentation().keys
        allKeys.forEach { key ->
            val keyStr = key as? String
            if (keyStr?.startsWith(prefix) == true) {
                userDefaults.removeObjectForKey(keyStr)
            }
        }
        userDefaults.synchronize()
    }
}

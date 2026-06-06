package com.ovasta.sellers.data.platform

import platform.Foundation.NSUserDefaults

/**
 * iOS Firebase Messaging implementation.
 * The FCM token is obtained from the Swift side (via Firebase SDK)
 * and stored in NSUserDefaults for Kotlin to read.
 */
actual class FirebaseMessagingProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val fcmTokenKey = "firebase_fcm_token"

    actual suspend fun getToken(): String {
        return userDefaults.stringForKey(fcmTokenKey) ?: ""
    }
    
    actual suspend fun deleteToken() {
        userDefaults.removeObjectForKey(fcmTokenKey)
        userDefaults.synchronize()
    }
}

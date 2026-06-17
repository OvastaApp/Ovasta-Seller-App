package com.ovasta.sellers.data.platform

import kotlinx.coroutines.delay
import platform.Foundation.NSUserDefaults

/**
 * iOS Firebase Messaging implementation.
 * The FCM token is obtained from the Swift side (via Firebase SDK)
 * and stored in NSUserDefaults for Kotlin to read.
 * Retries with delay to handle async token generation timing.
 */
actual class FirebaseMessagingProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val fcmTokenKey = "firebase_fcm_token"

    actual suspend fun getToken(): String {
        val token = userDefaults.stringForKey(fcmTokenKey)
        if (!token.isNullOrEmpty()) return token

        return retryWithDelay()
    }

    private suspend fun retryWithDelay(): String {
        repeat(15) {
            delay(200)
            val token = userDefaults.stringForKey(fcmTokenKey)
            if (!token.isNullOrEmpty()) return token
        }
        return ""
    }
    
    actual suspend fun deleteToken() {
        userDefaults.removeObjectForKey(fcmTokenKey)
        userDefaults.synchronize()
    }
}

package com.ovasta.sellers.data.platform

/**
 * Platform-specific Firebase Messaging (FCM) wrapper.
 * Android: Google Firebase Messaging SDK
 * iOS: APNs + Firebase Messaging SDK
 */
expect class FirebaseMessagingProvider {
    suspend fun getToken(): String
    suspend fun deleteToken()
}

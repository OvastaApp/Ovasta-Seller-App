package com.ovasta.sellers.platform

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual class FirebaseProvider {
    actual suspend fun getPushToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }

    actual fun sendTokenToServer(token: String) {
        // This will be injected via Koin - placeholder for actual implementation
    }
}

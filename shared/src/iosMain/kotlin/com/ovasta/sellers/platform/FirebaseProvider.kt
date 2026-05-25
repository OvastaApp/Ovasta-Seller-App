package com.ovasta.sellers.platform

actual class FirebaseProvider {
    actual suspend fun getPushToken(): String? {
        // iOS uses APNs, not FCM
        return null
    }

    actual fun sendTokenToServer(token: String) {
        // iOS uses APNs, not FCM
    }
}

package com.ovasta.sellers.data.platform

/**
 * iOS Firebase Messaging stub implementation.
 * TODO: Implement with Firebase iOS SDK + APNs in Phase 12 (requires CocoaPods setup).
 * For now, using stubs to enable compilation.
 */
actual class FirebaseMessagingProvider {
    actual suspend fun getToken(): String {
        // TODO: Implement with Firebase iOS SDK
        return "ios_stub_fcm_token"
    }
    
    actual suspend fun deleteToken() {
        // TODO: Implement with Firebase iOS SDK
        println("iOS: FirebaseMessagingProvider.deleteToken() - stub implementation")
    }
}

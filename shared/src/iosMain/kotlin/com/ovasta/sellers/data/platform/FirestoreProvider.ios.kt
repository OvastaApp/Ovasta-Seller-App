package com.ovasta.sellers.data.platform

/**
 * iOS Firestore stub implementation.
 * TODO: Implement with Firebase iOS SDK in Phase 12 (requires CocoaPods setup).
 * For now, using stubs to enable compilation.
 */
actual class FirestoreProvider {
    actual suspend fun saveUserData(userId: String, data: Map<String, Any>) {
        // TODO: Implement with Firebase iOS SDK
        println("iOS: FirestoreProvider.saveUserData() - stub implementation")
    }
    
    actual suspend fun getUserData(userId: String): Map<String, Any>? {
        // TODO: Implement with Firebase iOS SDK
        return null
    }
}

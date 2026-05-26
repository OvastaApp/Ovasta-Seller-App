package com.ovasta.sellers.data.platform

/**
 * iOS Firebase Auth stub implementation.
 * TODO: Implement with Firebase iOS SDK in Phase 12 (requires CocoaPods setup).
 * For now, using stubs to enable compilation.
 */
actual class FirebaseAuthProvider {
    actual suspend fun signInAnonymously() {
        // TODO: Implement with Firebase iOS SDK
        println("iOS: FirebaseAuthProvider.signInAnonymously() - stub implementation")
    }
    
    actual suspend fun getCurrentUserId(): String? {
        // TODO: Implement with Firebase iOS SDK
        return "ios_stub_user_id"
    }
    
    actual suspend fun signOut() {
        // TODO: Implement with Firebase iOS SDK
        println("iOS: FirebaseAuthProvider.signOut() - stub implementation")
    }
}

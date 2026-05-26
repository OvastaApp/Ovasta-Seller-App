package com.ovasta.sellers.data.platform

/**
 * Platform-specific Firebase Auth wrapper.
 * Android: Google Firebase Auth SDK
 * iOS: GitLive Firebase or native Firebase SDK
 */
expect class FirebaseAuthProvider {
    suspend fun signInAnonymously()
    suspend fun getCurrentUserId(): String?
    suspend fun signOut()
}

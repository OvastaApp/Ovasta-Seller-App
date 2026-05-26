package com.ovasta.sellers.data.platform

/**
 * Platform-specific Firebase Firestore wrapper.
 * Android: Google Firebase Firestore SDK
 * iOS: GitLive Firebase or native Firebase SDK
 */
expect class FirestoreProvider {
    suspend fun saveUserData(userId: String, data: Map<String, Any>)
    suspend fun getUserData(userId: String): Map<String, Any>?
}

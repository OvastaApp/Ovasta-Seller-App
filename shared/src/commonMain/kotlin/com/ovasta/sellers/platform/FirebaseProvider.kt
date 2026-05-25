package com.ovasta.sellers.platform

expect class FirebaseProvider {
    suspend fun getPushToken(): String?
    fun sendTokenToServer(token: String)
}

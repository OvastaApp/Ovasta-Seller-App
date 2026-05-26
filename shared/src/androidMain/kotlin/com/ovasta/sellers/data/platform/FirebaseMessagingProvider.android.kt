package com.ovasta.sellers.data.platform

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual class FirebaseMessagingProvider {
    private val messaging = FirebaseMessaging.getInstance()

    actual suspend fun getToken(): String = messaging.token.await()

    actual suspend fun deleteToken() {
        messaging.deleteToken().await()
    }
}

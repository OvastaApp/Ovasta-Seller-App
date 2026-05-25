package com.ovasta.sellers.base.notification

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ovasta.sellers.data.notification.FcmTokenRemoteDataSource
import com.ovasta.sellers.data.notification.FcmTokenRequest
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SellersFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
    }

    private val fcmTokenDataSource: FcmTokenRemoteDataSource by inject()
    private val dataStore: DataStore<SessionPreferences> by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token received: $token")
        CoroutineScope(Dispatchers.IO).launch {
            saveFcmToken(token)
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        if (title != null && body != null) {
            NotificationHelper.showNotification(
                context = this,
                title = title,
                body = body,
                data = remoteMessage.data
            )
        } else if (remoteMessage.data.isNotEmpty()) {
            val dataTitle = remoteMessage.data["title"] ?: "New Notification"
            val dataBody = remoteMessage.data["body"] ?: ""
            NotificationHelper.showNotification(
                context = this,
                title = dataTitle,
                body = dataBody,
                data = remoteMessage.data
            )
        }
    }

    private suspend fun saveFcmToken(token: String) {
        dataStore.updateData { preferences ->
            preferences.copy(fcmToken = token)
        }
    }

    private suspend fun sendTokenToServer(token: String) {
        val isLoggedIn = dataStore.data.first().accessToken.isNotEmpty()
        if (!isLoggedIn) {
            Log.d(TAG, "User not logged in, token cached for next login")
            return
        }
        try {
            fcmTokenDataSource.updateFcmToken(token)
            Log.d(TAG, "Token sent to server successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send token to server", e)
        }
    }
}
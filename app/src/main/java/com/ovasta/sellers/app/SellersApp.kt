package com.ovasta.sellers.app

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import com.google.firebase.messaging.FirebaseMessaging
import com.ovasta.sellers.base.di.startKoin
import com.ovasta.sellers.base.notification.NotificationHelper
import com.ovasta.sellers.data.notification.FcmTokenRemoteDataSource
import com.ovasta.sellers.data.notification.FcmTokenRequest
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class SellersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this@SellersApp)
        NotificationHelper.createNotificationChannel(this)

        val sessionDataStore: DataStore<SessionPreferences> by inject()
        val fcmTokenDataSource: FcmTokenRemoteDataSource by inject()

        CoroutineScope(Dispatchers.IO).launch {
            fetchAndSendFcmToken(sessionDataStore, fcmTokenDataSource)
        }
    }

    private suspend fun fetchAndSendFcmToken(
        dataStore: DataStore<SessionPreferences>,
        fcmTokenDataSource: FcmTokenRemoteDataSource
    ) {
        try {
            val cachedToken = dataStore.data.first().fcmToken

            FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
                CoroutineScope(Dispatchers.IO).launch {
                    handleTokenRetrieval(dataStore, fcmTokenDataSource, cachedToken, newToken)
                }
            }.addOnFailureListener { e ->
                Log.e("SellersApp", "Failed to get FCM token", e)
            }
        } catch (e: Exception) {
            Log.e("SellersApp", "Error in fetchAndSendFcmToken", e)
        }
    }

    private suspend fun handleTokenRetrieval(
        dataStore: DataStore<SessionPreferences>,
        fcmTokenDataSource: FcmTokenRemoteDataSource,
        cachedToken: String,
        newToken: String
    ) {
        dataStore.updateData { it.copy(fcmToken = newToken) }

        if (newToken != cachedToken) {
            val isLoggedIn = dataStore.data.first().accessToken.isNotEmpty()
            if (isLoggedIn) {
                try {
                    fcmTokenDataSource.updateFcmToken(newToken)
                } catch (e: Exception) {
                    Log.e("SellersApp", "Failed to send FCM token", e)
                }
            }
        }
    }
}
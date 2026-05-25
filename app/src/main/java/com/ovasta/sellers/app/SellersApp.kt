package com.ovasta.sellers.app

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import com.ovasta.sellers.base.di.startKoin
import com.ovasta.sellers.base.notification.NotificationHelper
import com.ovasta.sellers.data.notification.FcmTokenRemoteDataSource
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.platform.FirebaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SellersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this@SellersApp)
        NotificationHelper.createNotificationChannel(this)

        val sessionDataStore: DataStore<SessionPreferences> by inject()
        val fcmTokenDataSource: FcmTokenRemoteDataSource by inject()
        val firebaseProvider: FirebaseProvider by inject()

        CoroutineScope(Dispatchers.IO).launch {
            fetchAndSendFcmToken(sessionDataStore, fcmTokenDataSource, firebaseProvider)
        }
    }

    private suspend fun fetchAndSendFcmToken(
        dataStore: DataStore<SessionPreferences>,
        fcmTokenDataSource: FcmTokenRemoteDataSource,
        firebaseProvider: FirebaseProvider
    ) {
        try {
            val cachedToken = dataStore.data.first().fcmToken
            val newToken = firebaseProvider.getPushToken() ?: return
            
            handleTokenRetrieval(dataStore, fcmTokenDataSource, cachedToken, newToken)
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

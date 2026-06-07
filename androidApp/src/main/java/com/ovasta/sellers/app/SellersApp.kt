package com.ovasta.sellers.app

import android.app.Application
import android.content.Context
import com.ovasta.sellers.base.LocaleHelper
import java.util.Locale
import android.util.Log
import androidx.datastore.core.DataStore
import com.google.firebase.messaging.FirebaseMessaging
import com.ovasta.sellers.base.di.startKoin
import com.ovasta.sellers.base.interceptor.SessionHeaderCache
import com.ovasta.sellers.base.notification.NotificationHelper
import com.ovasta.sellers.data.notification.FcmTokenApi
import com.ovasta.sellers.data.notification.FcmTokenRequest
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class SellersApp : Application() {

    override fun attachBaseContext(base: Context) {
        // Get user's language preference and apply to app context
        val language = LocaleHelper.getLanguageFromPrefs(base)
        super.attachBaseContext(LocaleHelper.wrapContext(base, language))
    }

    override fun onCreate() {
        super.onCreate()
        // Set default locale from user preferences
        val language = LocaleHelper.getLanguageFromPrefs(this)
        Locale.setDefault(LocaleHelper.getLocale(language))
        startKoin(this@SellersApp)
        NotificationHelper.createNotificationChannel(this)

        val sessionDataStore: DataStore<SessionPreferences> by inject()
        val fcmTokenApi: FcmTokenApi by inject()

        CoroutineScope(Dispatchers.IO).launch {
            SessionHeaderCache.initialize(sessionDataStore)
            fetchAndSendFcmToken(sessionDataStore, fcmTokenApi)
        }
    }

    private suspend fun fetchAndSendFcmToken(
        dataStore: DataStore<SessionPreferences>,
        fcmTokenApi: FcmTokenApi
    ) {
        try {
            val cachedToken = dataStore.data.first().fcmToken

            FirebaseMessaging.getInstance().token.addOnSuccessListener { newToken ->
                CoroutineScope(Dispatchers.IO).launch {
                    handleTokenRetrieval(dataStore, fcmTokenApi, cachedToken, newToken)
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
        fcmTokenApi: FcmTokenApi,
        cachedToken: String,
        newToken: String
    ) {
        dataStore.updateData { it.copy(fcmToken = newToken) }

        if (newToken != cachedToken) {
            val isLoggedIn = dataStore.data.first().accessToken.isNotEmpty()
            if (isLoggedIn) {
                try {
                    fcmTokenApi.updateFcmToken(FcmTokenRequest(newToken))
                } catch (e: Exception) {
                    Log.e("SellersApp", "Failed to send FCM token", e)
                }
            }
        }
    }
}
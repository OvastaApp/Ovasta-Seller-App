package com.ovasta.sellers.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.datastore.core.DataStore
import com.ovasta.sellers.base.di.startKoin
import com.ovasta.sellers.base.interceptor.SessionHeaderCache
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class SellersApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin(this@SellersApp)
        val sessionDataStore: DataStore<SessionPreferences> by inject()
        CoroutineScope(Dispatchers.IO).launch {
            SessionHeaderCache.initialize(sessionDataStore)
        }
    }
}
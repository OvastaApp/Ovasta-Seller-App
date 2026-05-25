package com.ovasta.sellers.base.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.network.AuthTokenProvider
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionAuthTokenProvider(
    private val dataStore: DataStore<SessionPreferences>
) : AuthTokenProvider {
    override var token: String = ""
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val session = dataStore.data.first()
            token = session.accessToken
            dataStore.data.collect { token = it.accessToken }
        }
    }
}
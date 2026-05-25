package com.ovasta.sellers.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.network.AuthTokenProvider
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SessionAuthTokenProvider(
    private val dataStore: DataStore<SessionPreferences>
) : AuthTokenProvider {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val tokenFlow: StateFlow<String> = dataStore.data
        .map { it.accessToken }
        .stateIn(scope, SharingStarted.Eagerly, "")

    override val token: String
        get() = tokenFlow.value
}

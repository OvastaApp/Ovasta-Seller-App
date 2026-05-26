package com.ovasta.sellers.di

import com.ovasta.sellers.data.platform.DataStoreProvider
import com.ovasta.sellers.data.platform.DeviceInfoProvider
import com.ovasta.sellers.data.platform.FirebaseAuthProvider
import com.ovasta.sellers.data.platform.FirebaseMessagingProvider
import com.ovasta.sellers.data.platform.FirestoreProvider
import com.ovasta.sellers.data.platform.HapticFeedback
import com.ovasta.sellers.data.platform.SecureStorage
import com.ovasta.sellers.data.remote.SessionHeaderProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Platform services (require Context)
    single { SecureStorage(get()) }
    single { DataStoreProvider(get()) }
    single { DeviceInfoProvider(get()) }
    single { HapticFeedback(get()) }
    
    // Firebase services (no Context needed)
    single { FirebaseAuthProvider() }
    single { FirestoreProvider() }
    single { FirebaseMessagingProvider() }

    // Session header provider for HTTP client
    single<SessionHeaderProvider> {
        object : SessionHeaderProvider {
            override suspend fun getDeviceId(): String = get<DeviceInfoProvider>().getDeviceId()
            override suspend fun getAccessToken(): String = get<com.ovasta.sellers.domain.repository.ISettingsRepository>().getAccessToken()
            override suspend fun getLanguage(): String = "ar" // TODO: Make this dynamic
            override suspend fun getIdentifier(): String = "android" // Platform identifier
        }
    }
}

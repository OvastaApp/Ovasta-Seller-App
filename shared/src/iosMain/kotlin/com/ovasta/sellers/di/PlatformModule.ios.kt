package com.ovasta.sellers.di

import com.ovasta.sellers.data.platform.DataStoreProvider
import com.ovasta.sellers.data.platform.DeviceInfoProvider
import com.ovasta.sellers.data.platform.FirebaseAuthProvider
import com.ovasta.sellers.data.platform.FirebaseMessagingProvider
import com.ovasta.sellers.data.platform.FirestoreProvider
import com.ovasta.sellers.data.platform.HapticFeedback
import com.ovasta.sellers.data.platform.SecureStorage
import com.ovasta.sellers.data.remote.SessionHeaderProvider
import com.ovasta.sellers.domain.repository.ISettingsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Platform services (no Context required on iOS)
    single { SecureStorage() }
    single { DataStoreProvider() }
    single { DeviceInfoProvider() }
    single { HapticFeedback() }
    
    // Firebase services (stub implementations for now)
    single { FirebaseAuthProvider() }
    single { FirestoreProvider() }
    single { FirebaseMessagingProvider() }

    // Session header provider for HTTP client
    single<SessionHeaderProvider> {
        object : SessionHeaderProvider {
            override suspend fun getDeviceId(): String {
                return get<DeviceInfoProvider>().getDeviceId()
            }
            
            override suspend fun getAccessToken(): String {
                return get<ISettingsRepository>().getAccessToken()
            }
            
            override suspend fun getLanguage(): String {
                // TODO: Make this dynamic based on device locale
                return "ar"
            }
            
            override suspend fun getIdentifier(): String {
                return "ios" // Platform identifier
            }
        }
    }
}

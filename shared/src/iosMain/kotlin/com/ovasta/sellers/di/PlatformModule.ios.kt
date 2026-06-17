package com.ovasta.sellers.di

import com.ovasta.sellers.data.platform.DataStoreProvider
import com.ovasta.sellers.data.platform.DeviceInfoProvider
import com.ovasta.sellers.data.platform.FirebaseAuthProvider
import com.ovasta.sellers.data.platform.FirebaseMessagingProvider
import com.ovasta.sellers.data.platform.FirestoreProvider
import com.ovasta.sellers.data.platform.HapticFeedback
import com.ovasta.sellers.data.platform.PermissionManager
import com.ovasta.sellers.data.platform.SecureStorage
import com.ovasta.sellers.data.remote.SessionHeaderProvider
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

private var koinInitialized = false

fun initKoinIos() {
    if (koinInitialized) return
    koinInitialized = true
    startKoin {
        modules(sharedModule, platformModule())
    }
}

actual fun platformModule(): Module = module {
    // Platform services (no Context required on iOS)
    single { SecureStorage() }
    single { DataStoreProvider() }
    single { DeviceInfoProvider() }
    single { HapticFeedback() }
    single { PermissionManager() }
    
    // Firebase services (stub implementations for now)
    single { FirebaseAuthProvider() }
    single { FirestoreProvider() }
    single { FirebaseMessagingProvider() }

    // Session header provider for HTTP client
    // All reads use SecureStorage (NSUserDefaults) which is thread-safe
    single<SessionHeaderProvider> {
        val secureStorage = get<SecureStorage>()
        val deviceInfoProvider = get<DeviceInfoProvider>()
        object : SessionHeaderProvider {
            override fun getDeviceId(): String = deviceInfoProvider.getDeviceId()
            override fun getAccessToken(): String = secureStorage.getString("access_token") ?: ""
            override fun getLanguage(): String = secureStorage.getString("language") ?: "ar"
            override fun getIdentifier(): String =
                "\$2a\$12\$BeuZVyrk1vlnlws5ljkRnuHA5UypUwVW3gyGoFaGvpdF5sgeSzXr2"
        }
    }
}

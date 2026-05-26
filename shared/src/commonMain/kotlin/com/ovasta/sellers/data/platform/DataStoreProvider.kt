package com.ovasta.sellers.data.platform

/**
 * Platform-specific DataStore for preferences.
 * Android & iOS: Multiplatform DataStore
 */
expect class DataStoreProvider {
    fun getUserDataStore(): Any // Returns DataStore<Preferences>
}

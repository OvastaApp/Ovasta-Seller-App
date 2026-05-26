package com.ovasta.sellers.data.platform

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

actual class DataStoreProvider(private val context: Context) {
    actual fun getUserDataStore(): Any = context.userDataStore
}

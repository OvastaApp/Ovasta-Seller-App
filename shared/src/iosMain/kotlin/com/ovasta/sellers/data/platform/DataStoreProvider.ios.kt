package com.ovasta.sellers.data.platform

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private var dataStoreInstance: DataStore<Preferences>? = null

@OptIn(ExperimentalForeignApi::class)
private fun getOrCreateDataStore(): DataStore<Preferences> {
    return dataStoreInstance ?: run {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val path = requireNotNull(documentDirectory).path + "/user_prefs.preferences_pb"
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { path.toPath() }
        ).also { dataStoreInstance = it }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class DataStoreProvider {
    actual fun getUserDataStore(): Any {
        return getOrCreateDataStore()
    }
}

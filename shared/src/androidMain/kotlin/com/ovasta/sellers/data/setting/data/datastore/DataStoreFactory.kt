package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath

actual fun createSessionDataStore(producePath: () -> String): DataStore<SessionPreferences> {
    return DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = SessionPreferencesSerializer,
            producePath = { producePath().toPath() }
        )
    )
}

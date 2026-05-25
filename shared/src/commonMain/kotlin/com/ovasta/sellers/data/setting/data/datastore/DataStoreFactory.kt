package com.ovasta.sellers.data.setting.data.datastore

import androidx.datastore.core.DataStore

expect fun createSessionDataStore(producePath: () -> String): DataStore<SessionPreferences>

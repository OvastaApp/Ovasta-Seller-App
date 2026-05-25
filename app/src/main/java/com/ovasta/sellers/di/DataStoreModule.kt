package com.ovasta.sellers.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.base.constants.LocalConstants
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.data.setting.data.datastore.createSessionDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File

val dataStoreModule = module {
    single<DataStore<SessionPreferences>> {
        createSessionDataStore {
            File(androidContext().filesDir, "datastore/${LocalConstants.PREFERENCE_NAME}").absolutePath
        }
    }
}

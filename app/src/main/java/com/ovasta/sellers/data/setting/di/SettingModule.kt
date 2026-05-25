package com.ovasta.sellers.data.setting.di

import android.location.Geocoder
import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.setting.data.ISettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.data.setting.data.SettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.SettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.SettingsRepository
import com.ovasta.sellers.base.constants.SharedPreferenceConstants
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.data.setting.data.datastore.createSessionDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import java.util.Locale

val settingModule = module {
    single { Geocoder(androidContext(), Locale.getDefault()) }
    single<ISettingsRemoteDataSource> { SettingsRemoteDataSource(get()) }
    single<ISettingsLocalDataSource> { SettingsLocalDataSource(get<DataStore<SessionPreferences>>()) }
    single<DataStore<SessionPreferences>> {
        createSessionDataStore {
            File(androidContext().filesDir, "datastore/${SharedPreferenceConstants.PREFERENCE_NAME}").absolutePath
        }
    }
    single<ISettingsRepository> { SettingsRepository(get(), get()) }
}
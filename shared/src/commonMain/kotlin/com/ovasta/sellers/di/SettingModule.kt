package com.ovasta.sellers.di

import androidx.datastore.core.DataStore
import com.ovasta.sellers.data.setting.data.ISettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.data.setting.data.SettingsLocalDataSource
import com.ovasta.sellers.data.setting.data.SettingsRemoteDataSource
import com.ovasta.sellers.data.setting.data.SettingsRepository
import com.ovasta.sellers.data.setting.data.datastore.SessionPreferences
import com.ovasta.sellers.data.setting.data.datastore.createSessionDataStore
import org.koin.dsl.module

val settingModule = module {
    single<ISettingsRemoteDataSource> { SettingsRemoteDataSource(get()) }
    single<ISettingsLocalDataSource> { SettingsLocalDataSource(get<DataStore<SessionPreferences>>()) }
    single<ISettingsRepository> { SettingsRepository(get(), get()) }
}

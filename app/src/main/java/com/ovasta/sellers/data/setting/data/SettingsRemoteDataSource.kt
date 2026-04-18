package com.ovasta.sellers.data.setting.data

class SettingsRemoteDataSource(private val settingsApi: SettingsApi) : ISettingsRemoteDataSource {

    override suspend fun logout() {
        return settingsApi.logout()
    }

}
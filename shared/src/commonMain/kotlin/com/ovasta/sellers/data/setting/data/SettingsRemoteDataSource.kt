package com.ovasta.sellers.data.setting.data

import com.ovasta.sellers.data.network.SellerApiService

class SettingsRemoteDataSource(private val apiService: SellerApiService) : ISettingsRemoteDataSource {
    override suspend fun logout() {
        apiService.logout()
    }
}
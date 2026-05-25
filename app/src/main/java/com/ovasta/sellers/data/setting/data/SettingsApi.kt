package com.ovasta.sellers.data.setting.data

import retrofit2.http.GET

interface SettingsApi {
    @GET("logout")
    suspend fun logout()
}
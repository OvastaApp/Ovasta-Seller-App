package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

class ProfileRemoteDataSource(
    private val profileApi: ProfileApi
) : IProfileRemoteDataSource {
    override suspend fun getLastOrders(page: Int?): DeliveryOrdersResponse =
        profileApi.getLastOrders(page = page).data
}
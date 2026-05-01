package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IProfileRemoteDataSource {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse?
}
package com.ovasta.sellers.presentation.profile.data

import com.ovasta.sellers.presentation.home.data.model.DeliveryOrdersResponse

interface IProfileRepository {
    suspend fun getLastOrders(page: Int? = null): DeliveryOrdersResponse?

}
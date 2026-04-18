package com.ovasta.sellers.presentation.orderDetails.data

import com.ovasta.sellers.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.Flow

interface IOrderDetailsRepository {
    suspend fun listenToOrderChanges(
        districtId: Int,
        taskId: Int
    ): Flow<HomeTask>
}
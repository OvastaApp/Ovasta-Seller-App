package com.ovasta.sellers.presentation.home.data

import com.ovasta.sellers.data.ApiResponse
import com.ovasta.sellers.presentation.home.data.model.HomeTask
import com.ovasta.sellers.presentation.home.data.model.PartnerStatistics
import com.ovasta.sellers.presentation.home.data.model.PartnerStatus
import kotlinx.coroutines.flow.Flow

interface IHomeRemoteDataSource {
    suspend fun getAssignedTasks(
        userId: Int,
        districtId: Int
    ): Flow<List<HomeTask>>

    suspend fun logLocation(
        userId: Int, districtId: Int, latitude: Double, longitude: Double
    )

    suspend fun changePartnerStatus(isOnline: Boolean)

    suspend fun getPartnerStatus(): ApiResponse<PartnerStatus>

    suspend fun getPartnerStatistics(): ApiResponse<PartnerStatistics>

}
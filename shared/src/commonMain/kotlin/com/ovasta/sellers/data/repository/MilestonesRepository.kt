package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.MilestonesApiService
import com.ovasta.sellers.domain.model.MilestonesProgress
import com.ovasta.sellers.domain.repository.IMilestonesRepository

class MilestonesRepository(private val api: MilestonesApiService) : IMilestonesRepository {
    override suspend fun getProgress(): MilestonesProgress =
        api.getProgress().data ?: MilestonesProgress()
}

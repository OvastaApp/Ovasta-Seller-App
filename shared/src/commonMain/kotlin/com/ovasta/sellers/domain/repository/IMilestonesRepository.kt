package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.MilestonesProgress

interface IMilestonesRepository {
    suspend fun getProgress(): MilestonesProgress
}

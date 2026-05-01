package com.ovasta.sellers.presentation.profile.data

class ProfileRepository(
    private val profileRemoteDataSource: IProfileRemoteDataSource,
) : IProfileRepository {
    override suspend fun getLastOrders(page: Int?) =
        profileRemoteDataSource.getLastOrders(page = page)
}
package com.ovasta.sellers.presentation.createOrder.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateOrderRepository(
    private val createOrderRemoteDataSource: ICreateOrderRemoteDataSource,
) : ICreateOrderRepository {

    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        clientName: String,
        collectionAmount: Double,
        note: String
    ) = withContext(Dispatchers.IO) {
        createOrderRemoteDataSource.createOrder(
            destination,
            clientPhone,
            clientName,
            collectionAmount,
            note
        )
    }

}
package com.ovasta.sellers.presentation.createOrder.data

class CreateOrderRepository(
    private val createOrderRemoteDataSource: ICreateOrderRemoteDataSource
) : ICreateOrderRepository {
    override suspend fun createOrder(
        destination: String,
        clientPhone: String,
        collectionAmount: Double,
        deliveryFees: Double,
        note: String?
    ) = createOrderRemoteDataSource.createOrder(
        destination = destination,
        clientPhone = clientPhone,
        collectionAmount = collectionAmount,
        deliveryFees = deliveryFees,
        note = note
    )
}
package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.ProductApiService
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.UpdateProductPriceRequest
import com.ovasta.sellers.domain.repository.IProductRepository

class ProductRepository(private val api: ProductApiService) : IProductRepository {
    override suspend fun getCategories(): List<ProductCategory> =
        api.getCategories().data ?: emptyList()

    override suspend fun updateProductPrice(
        productId: Int,
        salesPrice: Double,
        purchasePrice: Double,
        show: Boolean,
        active: Boolean,
    ) = api.updateProductPrice(
        productId,
        UpdateProductPriceRequest(
            productId = productId,
            salesPrice = salesPrice,
            purchasePrice = purchasePrice,
            show = show,
            active = active,
        )
    )
}

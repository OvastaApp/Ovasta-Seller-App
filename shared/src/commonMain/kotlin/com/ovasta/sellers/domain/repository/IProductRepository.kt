package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.ProductCategory

interface IProductRepository {
    suspend fun getCategories(): List<ProductCategory>
    suspend fun updateProductPrice(
        productId: Int,
        salesPrice: Double,
        purchasePrice: Double,
        show: Boolean,
        active: Boolean,
    )
}

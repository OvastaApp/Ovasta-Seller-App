package com.ovasta.sellers.domain.repository

import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.SellerProduct

interface IProductRepository {
    suspend fun getCategories(): List<ProductCategory>
    suspend fun updateProductPrice(
        districtProductId: Int,
        name: String,
        description: String,
        price: Double,
        show: Boolean,
        active: Boolean,
    )
    suspend fun addProduct(
        subCategoryId: Int,
        name: String,
        description: String,
        price: Double,
        show: Boolean,
        active: Boolean,
    ): SellerProduct
}

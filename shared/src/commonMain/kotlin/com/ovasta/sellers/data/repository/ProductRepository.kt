package com.ovasta.sellers.data.repository

import com.ovasta.sellers.data.remote.ProductApiService
import com.ovasta.sellers.domain.model.AddProductRequest
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.SellerProduct
import com.ovasta.sellers.domain.model.UpdateProductPriceRequest
import com.ovasta.sellers.domain.repository.IProductRepository

class ProductRepository(private val api: ProductApiService) : IProductRepository {
    override suspend fun getCategories(): List<ProductCategory> =
        if (ProductDummyData.USE_DUMMY) ProductDummyData.categories
        else api.getCategories().data ?: emptyList()

    override suspend fun updateProductPrice(
        productId: Int,
        name: String,
        price: Double,
        show: Boolean,
        active: Boolean,
    ) {
        if (ProductDummyData.USE_DUMMY) return
        api.updateProductPrice(
            productId,
            UpdateProductPriceRequest(
                productId = productId,
                name = name,
                price = price,
                show = show,
                active = active,
            )
        )
    }

    override suspend fun addProduct(
        subCategoryId: Int,
        name: String,
        price: Double,
        show: Boolean,
        active: Boolean,
    ): SellerProduct {
        if (ProductDummyData.USE_DUMMY) {
            return SellerProduct(
                id = ProductDummyData.nextProductId(),
                name = name,
                active = active,
                show = show,
                salesPrice = price,
            )
        }
        return api.addProduct(
            AddProductRequest(
                subCategoryId = subCategoryId,
                name = name,
                price = price,
                show = show,
                active = active,
            )
        ).data ?: SellerProduct(id = 0, name = name, salesPrice = price, show = show, active = active)
    }
}

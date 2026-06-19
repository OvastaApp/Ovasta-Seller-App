package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductCategory(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("count") val count: Int = 0,
    @SerialName("sub_categories") val subCategories: List<ProductSubCategory> = emptyList(),
)

@Serializable
data class ProductSubCategory(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("products") val products: List<SellerProduct> = emptyList(),
)

@Serializable
data class SellerProduct(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String? = null,
    @SerialName("activation") val active: Boolean = false,
    @SerialName("show") val show: Boolean = false,
    @SerialName("sales_price") val salesPrice: Double = 0.0,
    @SerialName("purchase") val purchasePrice: Double = 0.0,
    @SerialName("image") val image: String? = null,
)

@Serializable
data class UpdateProductPriceRequest(
    @SerialName("product_id") val productId: Int,
    @SerialName("name") val name: String,
    @SerialName("sales_price") val price: Double,
    @SerialName("show") val show: Boolean,
    @SerialName("active") val active: Boolean,
)

@Serializable
data class AddProductRequest(
    @SerialName("sub_category_id") val subCategoryId: Int,
    @SerialName("name") val name: String,
    @SerialName("sales_price") val price: Double,
    @SerialName("show") val show: Boolean,
    @SerialName("active") val active: Boolean,
)

package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductCategory(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("count") val count: Int = 0,
    @SerialName("product_groups") val subCategories: List<ProductSubCategory> = emptyList(),
)

@Serializable
data class ProductSubCategory(
    // `id`/`name` are null for the implicit "ungrouped" group returned by the API.
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("priority") val priority: Int = 0,
    @SerialName("products") val products: List<SellerProduct> = emptyList(),
)

@Serializable
data class SellerProduct(
    @SerialName("id") val id: Int,
    // The id the update endpoint expects (PUT categories/products/{district_product_id}).
    @SerialName("district_product_id") val districtProductId: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("activation") val active: Boolean = false,
    @SerialName("show") val show: Boolean = false,
    @SerialName("sales_price") val salesPrice: Double = 0.0,
    @SerialName("purchase_price") val purchasePrice: Double = 0.0,
    @SerialName("image") val image: String? = null,
)

@Serializable
data class UpdateProductRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("activation") val active: Boolean,
    @SerialName("show") val show: Boolean,
    @SerialName("sales_price") val price: Double,
)

@Serializable
data class AddProductRequest(
    @SerialName("sub_category_id") val subCategoryId: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("sales_price") val price: Double,
    @SerialName("show") val show: Boolean,
    @SerialName("active") val active: Boolean,
)

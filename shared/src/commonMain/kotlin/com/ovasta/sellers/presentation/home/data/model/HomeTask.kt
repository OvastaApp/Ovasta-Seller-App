package com.ovasta.sellers.presentation.home.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class HomeTask(
    @JsonNames("order_id")
    val taskId: Int = 0,

    @JsonNames("customer_lat")
    val clientLat: Double = 0.0,

    @JsonNames("customer_long")
    val clientLang: Double = 0.0,

    @JsonNames("customer_name")
    val customerName: String? = null,

    @JsonNames("customer_address")
    val customerAddress: String? = null,

    @JsonNames("customer_mobile")
    val clientPhone: String? = null,

    @JsonNames("customer_whatsapp")
    val clientWhatsapp: String? = null,

    @JsonNames("delivery_fees")
    val deliveryFees: Float = 0f,

    @JsonNames("total_price")
    val totalPrice: Float = 0f,

    @JsonNames("items_count")
    val itemsCount: Int = 0,

    @JsonNames("status_id")
    val statusId: Int = 0,
    @JsonNames("status_name")
    val statusName: String = "",

    @JsonNames("products")
    var products: List<FirebaseProduct> = emptyList(),

    @JsonNames("updated_at")
    val updatedAt: Long? = null
)

@Serializable
data class FirebaseProduct(
    @JsonNames("catalog_product")
    val catalogProduct: Boolean? = null,

    @JsonNames("is_stock")
    val isStock: Boolean? = null,

    @JsonNames("item_price")
    val itemPrice: Int? = null,
    @JsonNames("main_sys_id")
    val mainSysId: Int? = null,

    @JsonNames("name")
    val name: String? = null,

    @JsonNames("quantity")
    val quantity: Int? = null,

    @JsonNames("source")
    val source: String? = null,

    @JsonNames("image_url")
    val imageUrl: String? = null,

    @JsonNames("total_price")
    val totalPrice: Double? = null,
    @JsonNames("updated_at")
    val updatedAt: Long? = null
)

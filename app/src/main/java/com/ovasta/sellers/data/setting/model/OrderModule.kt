package com.ovasta.sellers.data.setting.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Orders(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("discount") val discount: Double,
    //@SerializedName("order_status") val orderStatus: OrderSteps?,
    @SerializedName("coupons") val coupons: Double,
    @SerializedName("weight") val weight: Float,
    @SerializedName("partner_name") val partnerName: String,
    @SerializedName("status_id") val statusId: Int,
    @SerializedName("status_name") val statusName: String,
    @SerializedName("price") val oldPrice: Double = 0.0,
) : Parcelable

@Keep
@Parcelize
data class OrderStatus(
    @SerializedName("status") val status: String, @SerializedName("id") val id: Int
) : Parcelable
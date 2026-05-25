package com.ovasta.sellers.presentation.createOrder.presentation

import com.ovasta.sellers.presentation.createOrder.data.model.DeliveryTiming

data class CreateOrderViewState(
    val customerPhone: String = "",
    val customerAddress: String = "",
    val collectionAmount: String = "",
    val deliveryFees: String = "",
    val minOrderDeliveryPrice: Double = 0.0,
    val deliveryFeesError: String? = null,
    val deliveryTiming: DeliveryTiming = DeliveryTiming.NOW,
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val note: String = "",
    val customerPhoneError: String? = null,
    val customerAddressError: String? = null,
    val collectionAmountError: String? = null,
    val scheduledDateError: String? = null,
    val scheduledTimeError: String? = null,
    val isSubmitting: Boolean = false,
    val showConfirmDialog: Boolean = false
)

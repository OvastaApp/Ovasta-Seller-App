package com.ovasta.sellers.presentation.createOrder.presentation

import com.ovasta.sellers.presentation.createOrder.data.model.DeliveryTiming

fun CreateOrderViewState.isValid(): Boolean {
    return customerPhoneError == null &&
        customerAddressError == null &&
        collectionAmountError == null &&
        deliveryFeesError == null &&
        scheduledDateError == null &&
        scheduledTimeError == null &&
        customerPhone.isNotBlank() &&
        customerAddress.isNotBlank() &&
        collectionAmount.toDoubleOrNull() != null &&
        deliveryFees.toDoubleOrNull() != null &&
        (deliveryTiming != DeliveryTiming.LATER || (scheduledDate.isNotBlank() && scheduledTime.isNotBlank()))
}

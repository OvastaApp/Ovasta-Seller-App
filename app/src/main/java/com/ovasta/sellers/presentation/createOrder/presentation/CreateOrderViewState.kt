package com.ovasta.sellers.presentation.createOrder.presentation

data class CreateOrderViewState(
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val collectionAmount: String = "",

    // Delivery fees
    val deliveryFees: String = "",
    val deliveryFeesError: String? = null,

    // Delivery timing
    val deliveryTiming: DeliveryTiming = DeliveryTiming.NOW,
    val scheduledDate: String = "",
    val scheduledTime: String = "",

    // Validation errors
    val customerNameError: String? = null,
    val customerPhoneError: String? = null,
    val customerAddressError: String? = null,
    val collectionAmountError: String? = null,
    val scheduledDateError: String? = null,
    val scheduledTimeError: String? = null,

    // UI states
    val isSubmitting: Boolean = false,
    val showConfirmDialog: Boolean = false
)

enum class DeliveryTiming {
    NOW, LATER
}

// Extension function for validation
fun CreateOrderViewState.isValid(): Boolean {
    val basicFieldsValid = customerName.isNotBlank() &&
            customerPhone.length == 11 &&
            customerAddress.isNotBlank() &&
            collectionAmount.toDoubleOrNull() != null &&
            deliveryFees.toDoubleOrNull() != null &&
            deliveryFees.toDoubleOrNull()!! >= 15

    val scheduledFieldsValid = if (deliveryTiming == DeliveryTiming.LATER) {
        scheduledDate.isNotBlank() && scheduledTime.isNotBlank()
    } else {
        true
    }

    return basicFieldsValid && scheduledFieldsValid
}

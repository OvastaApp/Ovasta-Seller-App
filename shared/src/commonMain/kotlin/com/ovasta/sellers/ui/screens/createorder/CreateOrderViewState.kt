package com.ovasta.sellers.ui.screens.createorder

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
    val showConfirmDialog: Boolean = false,
)

enum class DeliveryTiming { NOW, LATER }

fun CreateOrderViewState.isValid(): Boolean {
    val basicValid = customerPhone.length == 11 &&
            customerAddress.isNotBlank() &&
            collectionAmount.toDoubleOrNull() != null &&
            deliveryFees.toDoubleOrNull() != null &&
            (deliveryFees.toDoubleOrNull() ?: 0.0) >= minOrderDeliveryPrice

    val scheduledValid = if (deliveryTiming == DeliveryTiming.LATER) {
        scheduledDate.isNotBlank() && scheduledTime.isNotBlank()
    } else true

    return basicValid && scheduledValid
}

sealed class CreateOrderScreenActions {
    data class OnCustomerPhoneChanged(val phone: String) : CreateOrderScreenActions()
    data class OnCustomerAddressChanged(val address: String) : CreateOrderScreenActions()
    data class OnCollectionAmountChanged(val amount: String) : CreateOrderScreenActions()
    data class OnDeliveryTimingChanged(val timing: DeliveryTiming) : CreateOrderScreenActions()
    data class OnScheduledDateChanged(val date: String) : CreateOrderScreenActions()
    data class OnScheduledTimeChanged(val time: String) : CreateOrderScreenActions()
    data class OnDeliveryFeesChanged(val fees: String) : CreateOrderScreenActions()
    data class OnNoteChanged(val note: String) : CreateOrderScreenActions()
    data object OnSubmitOrder : CreateOrderScreenActions()
    data object OnConfirmSubmit : CreateOrderScreenActions()
    data object OnDismissConfirmDialog : CreateOrderScreenActions()
    data object ResetState : CreateOrderScreenActions()
}

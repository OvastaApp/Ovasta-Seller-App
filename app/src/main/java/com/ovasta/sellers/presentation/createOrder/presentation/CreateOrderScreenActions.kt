package com.ovasta.sellers.presentation.createOrder.presentation

sealed class CreateOrderScreenActions {
    // Customer info
    data class OnCustomerNameChanged(val name: String) : CreateOrderScreenActions()
    data class OnCustomerPhoneChanged(val phone: String) : CreateOrderScreenActions()
    data class OnCustomerAddressChanged(val address: String) : CreateOrderScreenActions()
    data class OnCollectionAmountChanged(val amount: String) : CreateOrderScreenActions()

    // Delivery timing
    data class OnDeliveryTimingChanged(val timing: DeliveryTiming) : CreateOrderScreenActions()
    data class OnScheduledDateChanged(val date: String) : CreateOrderScreenActions()
    data class OnScheduledTimeChanged(val time: String) : CreateOrderScreenActions()

    // Submit
    data object OnSubmitOrder : CreateOrderScreenActions()
    data object OnConfirmSubmit : CreateOrderScreenActions()
    data object OnDismissConfirmDialog : CreateOrderScreenActions()
}

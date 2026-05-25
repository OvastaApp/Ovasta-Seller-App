package com.ovasta.sellers.presentation.createOrder.presentation

import com.ovasta.sellers.presentation.createOrder.data.model.DeliveryTiming

sealed class CreateOrderScreenActions {
    // Customer info
    data class OnCustomerPhoneChanged(val phone: String) : CreateOrderScreenActions()
    data class OnCustomerAddressChanged(val address: String) : CreateOrderScreenActions()
    data class OnCollectionAmountChanged(val amount: String) : CreateOrderScreenActions()

    // Delivery timing
    data class OnDeliveryTimingChanged(val timing: DeliveryTiming) : CreateOrderScreenActions()
    data class OnScheduledDateChanged(val date: String) : CreateOrderScreenActions()
    data class OnScheduledTimeChanged(val time: String) : CreateOrderScreenActions()

    data class OnDeliveryFeesChanged(val fees: String) : CreateOrderScreenActions()

    // Note
    data class OnNoteChanged(val note: String) : CreateOrderScreenActions()

    // Submit
    data object OnSubmitOrder : CreateOrderScreenActions()
    data object OnConfirmSubmit : CreateOrderScreenActions()
    data object OnDismissConfirmDialog : CreateOrderScreenActions()
}

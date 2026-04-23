package com.ovasta.sellers.presentation.createOrder.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.R
import com.ovasta.sellers.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateOrderViewModel(
    private val application: Application
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(CreateOrderViewState())
    val viewState: StateFlow<CreateOrderViewState> = _viewState.asStateFlow()

    fun onAction(action: CreateOrderScreenActions) {
        when (action) {
            is CreateOrderScreenActions.OnCustomerNameChanged -> {
                _viewState.update { it.copy(customerName = action.name, customerNameError = null) }
            }
            is CreateOrderScreenActions.OnCustomerPhoneChanged -> {
                _viewState.update { it.copy(customerPhone = action.phone, customerPhoneError = null) }
            }
            is CreateOrderScreenActions.OnCustomerAddressChanged -> {
                _viewState.update { it.copy(customerAddress = action.address, customerAddressError = null) }
            }
            is CreateOrderScreenActions.OnCollectionAmountChanged -> {
                _viewState.update { it.copy(collectionAmount = action.amount, collectionAmountError = null) }
            }
            is CreateOrderScreenActions.OnDeliveryTimingChanged -> {
                _viewState.update {
                    it.copy(
                        deliveryTiming = action.timing,
                        scheduledDate = "",
                        scheduledTime = "",
                        scheduledDateError = null,
                        scheduledTimeError = null
                    )
                }
            }
            is CreateOrderScreenActions.OnScheduledDateChanged -> {
                _viewState.update { it.copy(scheduledDate = action.date, scheduledDateError = null) }
            }
            is CreateOrderScreenActions.OnScheduledTimeChanged -> {
                _viewState.update { it.copy(scheduledTime = action.time, scheduledTimeError = null) }
            }
            is CreateOrderScreenActions.OnSubmitOrder -> {
                if (validateForm()) {
                    _viewState.update { it.copy(showConfirmDialog = true) }
                }
            }
            is CreateOrderScreenActions.OnConfirmSubmit -> {
                _viewState.update { it.copy(showConfirmDialog = false) }
                createOrder()
            }
            is CreateOrderScreenActions.OnDismissConfirmDialog -> {
                _viewState.update { it.copy(showConfirmDialog = false) }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _viewState.value
        var isValid = true

        if (state.customerName.isBlank()) {
            _viewState.update {
                it.copy(customerNameError = application.getString(R.string.customer_name_required))
            }
            isValid = false
        }

        if (state.customerPhone.isBlank() || state.customerPhone.length < 10) {
            _viewState.update {
                it.copy(customerPhoneError = application.getString(R.string.valid_phone_required))
            }
            isValid = false
        }

        if (state.customerAddress.isBlank()) {
            _viewState.update {
                it.copy(customerAddressError = application.getString(R.string.address_required))
            }
            isValid = false
        }

        if (state.collectionAmount.isBlank() || state.collectionAmount.toDoubleOrNull() == null) {
            _viewState.update {
                it.copy(collectionAmountError = application.getString(R.string.valid_amount_required))
            }
            isValid = false
        }

        // Validate scheduled delivery if LATER is selected
        if (state.deliveryTiming == DeliveryTiming.LATER) {
            if (state.scheduledDate.isBlank()) {
                _viewState.update {
                    it.copy(scheduledDateError = application.getString(R.string.delivery_date_required))
                }
                isValid = false
            }
            if (state.scheduledTime.isBlank()) {
                _viewState.update {
                    it.copy(scheduledTimeError = application.getString(R.string.delivery_time_required))
                }
                isValid = false
            }
        }

        return isValid
    }

    private fun createOrder() {

        viewModelScope.launch {
            _viewState.update { it.copy(isSubmitting = true) }

            try {
                val state = _viewState.value
                // TODO: Call your create-order POST endpoint here
                // val orderRequest = CreateOrderRequest(
                //     customerName = state.customerName,
                //     customerPhone = state.customerPhone,
                //     customerAddress = state.customerAddress,
                //     collectionAmount = state.collectionAmount.toDouble(),
                //     isScheduled = state.deliveryTiming == DeliveryTiming.LATER,
                //     scheduledDate = state.scheduledDate.takeIf { state.deliveryTiming == DeliveryTiming.LATER },
                //     scheduledTime = state.scheduledTime.takeIf { state.deliveryTiming == DeliveryTiming.LATER }
                // )
                // repository.createOrder(orderRequest)

                // On success, navigate back or show success message

            } catch (e: Exception) {
                // Handle error
            } finally {
                _viewState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
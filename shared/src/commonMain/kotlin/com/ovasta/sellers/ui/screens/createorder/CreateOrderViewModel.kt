package com.ovasta.sellers.ui.screens.createorder

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ICreateOrderRepository
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateOrderViewModel(
    private val repository: ICreateOrderRepository,
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(CreateOrderViewState())
    val viewState: StateFlow<CreateOrderViewState> = _viewState.asStateFlow()

    init {
        getMinDeliveryPrice()
    }

    fun onAction(action: CreateOrderScreenActions) {
        when (action) {
            is CreateOrderScreenActions.OnCustomerPhoneChanged -> {
                val error = if (action.phone.isBlank() || action.phone.length != 11)
                    "Valid phone number is required" else null
                _viewState.update { it.copy(customerPhone = action.phone, customerPhoneError = error) }
            }
            is CreateOrderScreenActions.OnCustomerAddressChanged ->
                _viewState.update { it.copy(customerAddress = action.address, customerAddressError = null) }
            is CreateOrderScreenActions.OnCollectionAmountChanged ->
                _viewState.update { it.copy(collectionAmount = action.amount, collectionAmountError = null) }
            is CreateOrderScreenActions.OnDeliveryTimingChanged ->
                _viewState.update { it.copy(deliveryTiming = action.timing, scheduledDate = "", scheduledTime = "", scheduledDateError = null, scheduledTimeError = null) }
            is CreateOrderScreenActions.OnScheduledDateChanged ->
                _viewState.update { it.copy(scheduledDate = action.date, scheduledDateError = null) }
            is CreateOrderScreenActions.OnScheduledTimeChanged ->
                _viewState.update { it.copy(scheduledTime = action.time, scheduledTimeError = null) }
            is CreateOrderScreenActions.OnDeliveryFeesChanged -> {
                val value = action.fees.toDoubleOrNull()
                val min = _viewState.value.minOrderDeliveryPrice
                val error = if (value == null || value < min) "Minimum delivery fees is $min EGP" else null
                _viewState.update { it.copy(deliveryFees = action.fees, deliveryFeesError = error) }
            }
            is CreateOrderScreenActions.OnNoteChanged ->
                _viewState.update { it.copy(note = action.note) }
            is CreateOrderScreenActions.OnSubmitOrder -> {
                if (validateForm()) _viewState.update { it.copy(showConfirmDialog = true) }
            }
            is CreateOrderScreenActions.OnConfirmSubmit -> {
                _viewState.update { it.copy(showConfirmDialog = false) }
                createOrder()
            }
            is CreateOrderScreenActions.OnDismissConfirmDialog ->
                _viewState.update { it.copy(showConfirmDialog = false) }
        }
    }

    private fun validateForm(): Boolean {
        val state = _viewState.value
        var isValid = true

        if (state.customerPhone.isBlank() || state.customerPhone.length != 11) {
            _viewState.update { it.copy(customerPhoneError = "Valid phone number is required") }
            isValid = false
        }
        if (state.customerAddress.isBlank()) {
            _viewState.update { it.copy(customerAddressError = "Address is required") }
            isValid = false
        }
        if (state.collectionAmount.isBlank() || state.collectionAmount.toDoubleOrNull() == null) {
            _viewState.update { it.copy(collectionAmountError = "Valid amount is required") }
            isValid = false
        }
        val fees = state.deliveryFees.toDoubleOrNull()
        if (fees == null || fees < state.minOrderDeliveryPrice) {
            _viewState.update { it.copy(deliveryFeesError = "Minimum delivery fees is ${state.minOrderDeliveryPrice} EGP") }
            isValid = false
        }
        if (state.deliveryTiming == DeliveryTiming.LATER) {
            if (state.scheduledDate.isBlank()) {
                _viewState.update { it.copy(scheduledDateError = "Delivery date is required") }
                isValid = false
            }
            if (state.scheduledTime.isBlank()) {
                _viewState.update { it.copy(scheduledTimeError = "Delivery time is required") }
                isValid = false
            }
        }
        return isValid
    }

    private fun createOrder() {
        viewModelScope.launch {
            setLoading(true)
            val state = _viewState.value
            runCatching {
                repository.createOrder(
                    destination = state.customerAddress,
                    clientPhone = state.customerPhone,
                    collectionAmount = state.collectionAmount.toDouble(),
                    deliveryFees = state.deliveryFees.toDouble(),
                    note = state.note.takeIf { it.isNotBlank() }
                )
            }.onSuccess {
                setLoading(false)
                emitMessage("Order submitted successfully")
                emitScreenDirection(ScreenDirection.Pop)
            }.onFailure {
                setLoading(false)
                handleError(it)
            }
        }
    }

    private fun getMinDeliveryPrice() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { settingsRepository.getHomeInfo() }
                .onSuccess { response ->
                    setLoading(false)
                    _viewState.update { it.copy(minOrderDeliveryPrice = response?.minOrderDeliveryPrice ?: 0.0) }
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }
}

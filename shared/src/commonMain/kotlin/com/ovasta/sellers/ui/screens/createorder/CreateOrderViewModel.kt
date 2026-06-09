package com.ovasta.sellers.ui.screens.createorder

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.ICreateOrderRepository
import com.ovasta.sellers.domain.repository.ISettingsRepository
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.address_required
import com.ovasta.sellers.shared.resources.delivery_date_required
import com.ovasta.sellers.shared.resources.delivery_fees_min_egp_error
import com.ovasta.sellers.shared.resources.delivery_time_required
import com.ovasta.sellers.shared.resources.order_submitted_successfully
import com.ovasta.sellers.shared.resources.valid_amount_required
import com.ovasta.sellers.shared.resources.valid_phone_required
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import kotlin.math.round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

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
                viewModelScope.launch {
                    val error = if (action.phone.isBlank() || action.phone.length != 11)
                        getString(Res.string.valid_phone_required) else null
                    _viewState.update { it.copy(customerPhone = action.phone, customerPhoneError = error) }
                }
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
                viewModelScope.launch {
                    val value = action.fees.toDoubleOrNull()
                    val min = _viewState.value.minOrderDeliveryPrice
                    val error = if (value == null || value < min)
                        getString(Res.string.delivery_fees_min_egp_error, min.formatPrice()) else null
                    _viewState.update { it.copy(deliveryFees = action.fees, deliveryFeesError = error) }
                }
            }
            is CreateOrderScreenActions.OnNoteChanged ->
                _viewState.update { it.copy(note = action.note) }
            is CreateOrderScreenActions.OnSubmitOrder -> {
                viewModelScope.launch {
                    if (validateForm()) _viewState.update { it.copy(showConfirmDialog = true) }
                }
            }
            is CreateOrderScreenActions.OnConfirmSubmit -> {
                _viewState.update { it.copy(showConfirmDialog = false) }
                createOrder()
            }
            is CreateOrderScreenActions.OnDismissConfirmDialog ->
                _viewState.update { it.copy(showConfirmDialog = false) }
            CreateOrderScreenActions.ResetState -> {
                _viewState.value = CreateOrderViewState()
                getMinDeliveryPrice()
            }
        }
    }

    private suspend fun validateForm(): Boolean {
        val state = _viewState.value
        var isValid = true

        if (state.customerPhone.isBlank() || state.customerPhone.length != 11) {
            _viewState.update { it.copy(customerPhoneError = getString(Res.string.valid_phone_required)) }
            isValid = false
        }
        if (state.customerAddress.isBlank()) {
            _viewState.update { it.copy(customerAddressError = getString(Res.string.address_required)) }
            isValid = false
        }
        if (state.collectionAmount.isBlank() || state.collectionAmount.toDoubleOrNull() == null) {
            _viewState.update { it.copy(collectionAmountError = getString(Res.string.valid_amount_required)) }
            isValid = false
        }
        val fees = state.deliveryFees.toDoubleOrNull()
        if (fees == null || fees < state.minOrderDeliveryPrice) {
            _viewState.update { it.copy(deliveryFeesError = getString(Res.string.delivery_fees_min_egp_error, state.minOrderDeliveryPrice.formatPrice())) }
            isValid = false
        }
        if (state.deliveryTiming == DeliveryTiming.LATER) {
            if (state.scheduledDate.isBlank()) {
                _viewState.update { it.copy(scheduledDateError = getString(Res.string.delivery_date_required)) }
                isValid = false
            }
            if (state.scheduledTime.isBlank()) {
                _viewState.update { it.copy(scheduledTimeError = getString(Res.string.delivery_time_required)) }
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
                emitMessage(getString(Res.string.order_submitted_successfully))
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

private fun Double.formatPrice(): String {
    val rounded = round(this * 100) / 100.0
    val str = rounded.toString()
    val dot = str.indexOf('.')
    if (dot == -1) return "$str.00"
    val decimals = str.length - dot - 1
    return when {
        decimals >= 2 -> str.substring(0, dot + 3)
        else -> str.padEnd(dot + 3, '0')
    }
}

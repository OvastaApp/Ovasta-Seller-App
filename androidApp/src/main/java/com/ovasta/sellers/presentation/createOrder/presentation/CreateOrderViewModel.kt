package com.ovasta.sellers.presentation.createOrder.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.R
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.components.sharedComposable.ToastMsg
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.base.ext.ToastEvent
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.data.setting.data.SettingsRepository
import com.ovasta.sellers.domain.repository.ICreateOrderRepository
import com.ovasta.sellers.presentation.nav.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateOrderViewModel(
    private val application: Application,
    private val repository: ICreateOrderRepository,
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(CreateOrderViewState())
    val viewState: StateFlow<CreateOrderViewState> = _viewState.asStateFlow()

    init {
        getMinRedeemPoints()
    }

    fun onAction(action: CreateOrderScreenActions) {
        when (action) {

            is CreateOrderScreenActions.OnCustomerPhoneChanged -> {
                val phone = action.phone
                val error = if (phone.isBlank() || phone.length != 11)
                    application.getString(R.string.valid_phone_required)
                else null
                _viewState.update {
                    it.copy(
                        customerPhone = phone,
                        customerPhoneError = error
                    )
                }
            }

            is CreateOrderScreenActions.OnCustomerAddressChanged -> {
                _viewState.update {
                    it.copy(
                        customerAddress = action.address,
                        customerAddressError = null
                    )
                }
            }

            is CreateOrderScreenActions.OnCollectionAmountChanged -> {
                _viewState.update {
                    it.copy(
                        collectionAmount = action.amount,
                        collectionAmountError = null
                    )
                }
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
                _viewState.update {
                    it.copy(
                        scheduledDate = action.date,
                        scheduledDateError = null
                    )
                }
            }

            is CreateOrderScreenActions.OnScheduledTimeChanged -> {
                _viewState.update {
                    it.copy(
                        scheduledTime = action.time,
                        scheduledTimeError = null
                    )
                }
            }

            is CreateOrderScreenActions.OnDeliveryFeesChanged -> {
                val value = action.fees
                val deliveryFeesValue = value.toDoubleOrNull()
                val minPrice = viewState.value.minOrderDeliveryPrice
                val error = when {
                    value.isBlank() || deliveryFeesValue == null ->
                        application.getString(R.string.delivery_fees_min_egp_error, minPrice)

                    deliveryFeesValue < minPrice ->
                        application.getString(R.string.delivery_fees_min_egp_error, minPrice)

                    else -> null
                }
                _viewState.update { it.copy(deliveryFees = value, deliveryFeesError = error) }
            }

            is CreateOrderScreenActions.OnNoteChanged -> {
                val note = action.note
                // No error, just limit to 4 lines
                _viewState.update { it.copy(note = note) }
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

        // Phone must be exactly 11 digits
        if (state.customerPhone.isBlank() || state.customerPhone.length != 11) {
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

        // Delivery Fees validation
        val deliveryFeesValue = state.deliveryFees.toDoubleOrNull()
        val minPrice = viewState.value.minOrderDeliveryPrice.toDouble()
        if (state.deliveryFees.isBlank() || deliveryFeesValue == null) {
            _viewState.update {
                it.copy(
                    deliveryFeesError = application.getString(
                        R.string.delivery_fees_min_egp_error,
                        minPrice
                    )
                )
            }
            isValid = false
        } else if (deliveryFeesValue < viewState.value.minOrderDeliveryPrice) {
            _viewState.update {
                it.copy(
                    deliveryFeesError = application.getString(
                        R.string.delivery_fees_min_egp_error,
                        minPrice
                    )
                )
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

    fun createOrder() {
        viewModelScope.launch {
            setComposeUILoading(true)
            kotlin.runCatching {
                val state = _viewState.value
                repository.createOrder(
                    destination = state.customerAddress,
                    clientPhone = state.customerPhone,
                    collectionAmount = state.collectionAmount.toDouble(),
                    deliveryFees = state.deliveryFees.toDouble(),
                    note = state.note.takeIf { it.isNotBlank() }
                )
            }.onSuccess {
                setComposeUILoading(false)
                emitToastEvent(
                    ToastEvent.ResourceToastEvent(R.string.order_submitted_successfully)
                )
                emitScreenDirectionEvent(ScreenDirection.Pop)
            }.onFailure {
                setComposeUILoading(false)
                updateViewStateWithFail(it)
            }
        }
    }

    private fun getMinRedeemPoints(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                settingsRepository.getHomeInfo()
            }.onSuccess { response ->
                setComposeUILoading(false)
                _viewState.update {
                    it.copy(minOrderDeliveryPrice = response?.minOrderDeliveryPrice ?: 0.0)
                }
                onSuccess()
            }.onFailure {
                updateViewStateWithFail(it)
            }
        }
    }

    fun updateViewStateWithFail(throwable: Throwable) {
        setComposeUILoading(false)
        emitComposeUIExceptionEvent(throwable.toComposeUIException())
    }
}

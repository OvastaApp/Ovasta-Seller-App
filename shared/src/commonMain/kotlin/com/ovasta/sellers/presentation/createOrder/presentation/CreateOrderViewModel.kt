package com.ovasta.sellers.presentation.createOrder.presentation

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.StringIds
import com.ovasta.sellers.base.StringResourceProvider
import com.ovasta.sellers.base.ext.ToastEvent
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.createOrder.data.ICreateOrderRepository
import com.ovasta.sellers.presentation.createOrder.data.model.DeliveryTiming
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateOrderViewModel(
    private val stringProvider: StringResourceProvider,
    private val repository: ICreateOrderRepository,
    private val settingsRepository: ISettingsRepository
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(CreateOrderViewState())
    val viewState: StateFlow<CreateOrderViewState> = _viewState.asStateFlow()

    init {
        getMinDeliveryPrice()
    }

    fun onAction(action: CreateOrderScreenActions) {
        when (action) {
            is CreateOrderScreenActions.OnCustomerPhoneChanged -> {
                val phone = action.phone
                val error = if (phone.isBlank() || phone.length != 11)
                    stringProvider.getString(StringIds.validPhoneRequired)
                else null
                _viewState.update {
                    it.copy(customerPhone = phone, customerPhoneError = error)
                }
            }

            is CreateOrderScreenActions.OnCustomerAddressChanged -> {
                _viewState.update {
                    it.copy(customerAddress = action.address, customerAddressError = null)
                }
            }

            is CreateOrderScreenActions.OnCollectionAmountChanged -> {
                _viewState.update {
                    it.copy(collectionAmount = action.amount, collectionAmountError = null)
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
                    it.copy(scheduledDate = action.date, scheduledDateError = null)
                }
            }

            is CreateOrderScreenActions.OnScheduledTimeChanged -> {
                _viewState.update {
                    it.copy(scheduledTime = action.time, scheduledTimeError = null)
                }
            }

            is CreateOrderScreenActions.OnDeliveryFeesChanged -> {
                val value = action.fees
                val deliveryFeesValue = value.toDoubleOrNull()
                val minPrice = viewState.value.minOrderDeliveryPrice
                val error = when {
                    value.isBlank() || deliveryFeesValue == null ->
                        stringProvider.getString(StringIds.deliveryFeesMinEgpError, minPrice)
                    deliveryFeesValue < minPrice ->
                        stringProvider.getString(StringIds.deliveryFeesMinEgpError, minPrice)
                    else -> null
                }
                _viewState.update { it.copy(deliveryFees = value, deliveryFeesError = error) }
            }

            is CreateOrderScreenActions.OnNoteChanged -> {
                _viewState.update { it.copy(note = action.note) }
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

        if (state.customerPhone.isBlank() || state.customerPhone.length != 11) {
            _viewState.update {
                it.copy(customerPhoneError = stringProvider.getString(StringIds.validPhoneRequired))
            }
            isValid = false
        }

        if (state.customerAddress.isBlank()) {
            _viewState.update {
                it.copy(customerAddressError = stringProvider.getString(StringIds.addressRequired))
            }
            isValid = false
        }

        if (state.collectionAmount.isBlank() || state.collectionAmount.toDoubleOrNull() == null) {
            _viewState.update {
                it.copy(collectionAmountError = stringProvider.getString(StringIds.validAmountRequired))
            }
            isValid = false
        }

        val deliveryFeesValue = state.deliveryFees.toDoubleOrNull()
        val minPrice = state.minOrderDeliveryPrice
        if (state.deliveryFees.isBlank() || deliveryFeesValue == null) {
            _viewState.update {
                it.copy(
                    deliveryFeesError = stringProvider.getString(StringIds.deliveryFeesMinEgpError, minPrice)
                )
            }
            isValid = false
        } else if (deliveryFeesValue < minPrice) {
            _viewState.update {
                it.copy(
                    deliveryFeesError = stringProvider.getString(StringIds.deliveryFeesMinEgpError, minPrice)
                )
            }
            isValid = false
        }

        if (state.deliveryTiming == DeliveryTiming.LATER) {
            if (state.scheduledDate.isBlank()) {
                _viewState.update {
                    it.copy(scheduledDateError = stringProvider.getString(StringIds.deliveryDateRequired))
                }
                isValid = false
            }
            if (state.scheduledTime.isBlank()) {
                _viewState.update {
                    it.copy(scheduledTimeError = stringProvider.getString(StringIds.deliveryTimeRequired))
                }
                isValid = false
            }
        }

        return isValid
    }

    fun createOrder() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
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
                    ToastEvent.ResourceToastEvent(StringIds.orderSubmittedSuccessfully)
                )
                emitScreenDirectionEvent(ScreenDirection.Pop)
            }.onFailure {
                setComposeUILoading(false)
                updateViewStateWithFail(it)
            }
        }
    }

    private fun getMinDeliveryPrice() {
        viewModelScope.launch {
            setComposeUILoading(true)
            runCatching {
                settingsRepository.getHomeInfo()
            }.onSuccess { response ->
                setComposeUILoading(false)
                _viewState.update {
                    it.copy(minOrderDeliveryPrice = response?.minOrderDeliveryPrice ?: 0.0)
                }
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

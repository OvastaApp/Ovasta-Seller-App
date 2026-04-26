package com.ovasta.sellers.presentation.createOrder.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.lgSemiBold
import com.ovasta.sellers.base.mdMedium
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderScreenActions
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewState
import com.ovasta.sellers.presentation.createOrder.presentation.DeliveryTiming
import com.ovasta.sellers.presentation.createOrder.presentation.isValid

private val BrandColor = Color(0xFF006D98)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderContent(
    viewState: CreateOrderViewState,
    onAction: (CreateOrderScreenActions) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                color = Color.White
            ) {
                CenteredTextAppBar(
                    stringResource(R.string.task_details),
                    onBackButtonPressed = { onNavigateBack() }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Customer Information Section
                Text(
                    text = stringResource(R.string.customer_information),
                    style = lgSemiBold,
                    color = BrandColor
                )

                OrderTextField(
                    value = viewState.customerName,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCustomerNameChanged(it)) },
                    label = stringResource(R.string.customer_name),
                    error = viewState.customerNameError
                )

                OrderTextField(
                    value = viewState.customerPhone,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCustomerPhoneChanged(it)) },
                    label = stringResource(R.string.phone_number),
                    error = viewState.customerPhoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OrderTextField(
                    value = viewState.customerAddress,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCustomerAddressChanged(it)) },
                    label = stringResource(R.string.delivery_address),
                    error = viewState.customerAddressError,
                    minLines = 2
                )

                OrderTextField(
                    value = viewState.collectionAmount,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCollectionAmountChanged(it)) },
                    label = stringResource(R.string.collection_amount),
                    error = viewState.collectionAmountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )


                // Delivery Timing Section
                Text(
                    text = stringResource(R.string.when_to_deliver),
                    style = lgSemiBold,
                    color = BrandColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DeliveryTimingOption(
                        text = stringResource(R.string.now),
                        isSelected = viewState.deliveryTiming == DeliveryTiming.NOW,
                        onClick = {
                            onAction(
                                CreateOrderScreenActions.OnDeliveryTimingChanged(
                                    DeliveryTiming.NOW
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DeliveryTimingOption(
                        text = stringResource(R.string.later),
                        isSelected = viewState.deliveryTiming == DeliveryTiming.LATER,
                        onClick = {
                            onAction(
                                CreateOrderScreenActions.OnDeliveryTimingChanged(
                                    DeliveryTiming.LATER
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Show date/time pickers if LATER is selected
                if (viewState.deliveryTiming == DeliveryTiming.LATER) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Scheduled Date
                        OrderTextField(
                            value = viewState.scheduledDate,
                            onValueChange = {
                                onAction(
                                    CreateOrderScreenActions.OnScheduledDateChanged(
                                        it
                                    )
                                )
                            },
                            label = stringResource(R.string.delivery_date),
                            error = viewState.scheduledDateError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        // Scheduled Time
                        OrderTextField(
                            value = viewState.scheduledTime,
                            onValueChange = {
                                onAction(
                                    CreateOrderScreenActions.OnScheduledTimeChanged(
                                        it
                                    )
                                )
                            },
                            label = stringResource(R.string.delivery_time),
                            error = viewState.scheduledTimeError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }

            // Submit Button - Fixed at bottom with background
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Button(
                    onClick = { onAction(CreateOrderScreenActions.OnSubmitOrder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(8.dp),
                    enabled = viewState.isValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandColor,
                        disabledContainerColor = BrandColor.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(R.string.create_order),
                        style = mdSemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    if (viewState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) },
            title = { Text(stringResource(R.string.confirm_order_title), style = lgSemiBold) },
            text = { Text(stringResource(R.string.confirm_order_message), style = mdMedium) },
            confirmButton = {
                TextButton(
                    onClick = { onAction(CreateOrderScreenActions.OnConfirmSubmit) }
                ) {
                    Text(stringResource(R.string.confirm), color = BrandColor, style = mdSemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) }
                ) {
                    Text(stringResource(R.string.cancel), style = mdMedium)
                }
            }
        )
    }
}

private fun isFormValid(viewState: CreateOrderViewState): Boolean {
    val basicFieldsValid = viewState.customerName.isNotBlank() &&
            viewState.customerPhone.length >= 10 &&
            viewState.customerAddress.isNotBlank() &&
            viewState.collectionAmount.toDoubleOrNull() != null

    val scheduledFieldsValid = if (viewState.deliveryTiming == DeliveryTiming.LATER) {
        viewState.scheduledDate.isNotBlank() && viewState.scheduledTime.isNotBlank()
    } else {
        true
    }

    return basicFieldsValid && scheduledFieldsValid
}

@Composable
private fun DeliveryTimingOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) BrandColor else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Black // ممكن تسيبها أو تشيلها
        ),
        border = BorderStroke(2.dp, BrandColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = mdSemiBold,
            color = if (isSelected) Color.White else Color.Black // 👈 ده المهم
        )
    }
}
@Composable
private fun OrderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = smMedium) },
        isError = error != null,
        supportingText = error?.let {
            {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = xsMedium
                )
            }
        },
        keyboardOptions = keyboardOptions,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        textStyle = mdMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandColor,
            focusedLabelColor = BrandColor,
            cursorColor = BrandColor
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewCreateOrderContent() {
    CreateOrderContent(
        viewState = CreateOrderViewState(
            customerName = "John Doe",
            customerPhone = "1234567890",
            customerAddress = "123 Main St, City",
            collectionAmount = "250.00",
            deliveryTiming = DeliveryTiming.NOW
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PreviewCreateOrderContentLater() {
    CreateOrderContent(
        viewState = CreateOrderViewState(
            customerName = "John Doe",
            customerPhone = "1234567890",
            customerAddress = "123 Main St, City",
            collectionAmount = "250.00",
            deliveryTiming = DeliveryTiming.LATER,
            scheduledDate = "25/12/2024",
            scheduledTime = "14:30"
        ),
        onAction = {}
    )
}

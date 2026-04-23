package com.ovasta.sellers.presentation.createOrder.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import com.ovasta.sellers.R
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderScreenActions
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewState
import com.ovasta.sellers.presentation.createOrder.presentation.DeliveryTiming

private val BrandColor = Color(0xFF006D98)

@Composable
fun CreateOrderContent(
    viewState: CreateOrderViewState,
    onAction: (CreateOrderScreenActions) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Customer Information Section
            Text(
                text = stringResource(R.string.customer_information),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            OrderTextField(
                value = viewState.customerName,
                onValueChange = { onAction(CreateOrderScreenActions.OnCustomerNameChanged(it)) },
                label = stringResource(R.string.customer_name),
                error = viewState.customerNameError
            )
            Spacer(modifier = Modifier.height(12.dp))

            OrderTextField(
                value = viewState.customerPhone,
                onValueChange = { onAction(CreateOrderScreenActions.OnCustomerPhoneChanged(it)) },
                label = stringResource(R.string.phone_number),
                error = viewState.customerPhoneError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OrderTextField(
                value = viewState.customerAddress,
                onValueChange = { onAction(CreateOrderScreenActions.OnCustomerAddressChanged(it)) },
                label = stringResource(R.string.delivery_address),
                error = viewState.customerAddressError,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))

            OrderTextField(
                value = viewState.collectionAmount,
                onValueChange = { onAction(CreateOrderScreenActions.OnCollectionAmountChanged(it)) },
                label = stringResource(R.string.collection_amount),
                error = viewState.collectionAmountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Delivery Timing Section
            Text(
                text = stringResource(R.string.when_to_deliver),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BrandColor
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeliveryTimingOption(
                    text = stringResource(R.string.now),
                    isSelected = viewState.deliveryTiming == DeliveryTiming.NOW,
                    onClick = { onAction(CreateOrderScreenActions.OnDeliveryTimingChanged(DeliveryTiming.NOW)) },
                    modifier = Modifier.weight(1f)
                )
                DeliveryTimingOption(
                    text = stringResource(R.string.later),
                    isSelected = viewState.deliveryTiming == DeliveryTiming.LATER,
                    onClick = { onAction(CreateOrderScreenActions.OnDeliveryTimingChanged(DeliveryTiming.LATER)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Show date/time pickers if LATER is selected
            if (viewState.deliveryTiming == DeliveryTiming.LATER) {
                Spacer(modifier = Modifier.height(16.dp))

                OrderTextField(
                    value = viewState.scheduledDate,
                    onValueChange = { onAction(CreateOrderScreenActions.OnScheduledDateChanged(it)) },
                    label = stringResource(R.string.delivery_date),
                    error = viewState.scheduledDateError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OrderTextField(
                    value = viewState.scheduledTime,
                    onValueChange = { onAction(CreateOrderScreenActions.OnScheduledTimeChanged(it)) },
                    label = stringResource(R.string.delivery_time),
                    error = viewState.scheduledTimeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Submit Button - Fixed at bottom
        Button(
            onClick = { onAction(CreateOrderScreenActions.OnSubmitOrder) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            enabled = !viewState.isSubmitting && isFormValid(viewState),
            colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
        ) {
            if (viewState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.create_order),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Confirmation Dialog
    if (viewState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) },
            title = { Text(stringResource(R.string.confirm_order_title)) },
            text = { Text(stringResource(R.string.confirm_order_message)) },
            confirmButton = {
                TextButton(
                    onClick = { onAction(CreateOrderScreenActions.OnConfirmSubmit) }
                ) {
                    Text(stringResource(R.string.confirm), color = BrandColor)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) }
                ) {
                    Text(stringResource(R.string.cancel))
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
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) BrandColor else Color.Transparent,
            contentColor = if (isSelected) Color.White else BrandColor
        ),
        border = BorderStroke(2.dp, BrandColor)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
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
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = keyboardOptions,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
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

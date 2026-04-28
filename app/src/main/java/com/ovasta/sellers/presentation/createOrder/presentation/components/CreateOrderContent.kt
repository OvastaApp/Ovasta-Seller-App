package com.ovasta.sellers.presentation.createOrder.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.CenteredTextAppBar
import com.ovasta.sellers.base.components.sharedComposable.BaseDialog
import com.ovasta.sellers.base.mdMedium
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderScreenActions
import com.ovasta.sellers.presentation.createOrder.presentation.CreateOrderViewState
import com.ovasta.sellers.presentation.createOrder.presentation.DeliveryTiming
import com.ovasta.sellers.presentation.createOrder.presentation.isValid

private val BrandColor = Color(0xFF006D98)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateOrderContent(
    viewState: CreateOrderViewState,
    onAction: (CreateOrderScreenActions) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val phoneFocusRequester = remember { FocusRequester() }
    val addressFocusRequester = remember { FocusRequester() }
    val collectionAmountFocusRequester = remember { FocusRequester() }
    val deliveryFeesFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            Surface(
                shadowElevation = 2.dp, color = Color.White
            ) {
                CenteredTextAppBar(
                    stringResource(R.string.order_details),
                    onBackButtonPressed = { onNavigateBack() })
            }
        }) { paddingValues ->
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

                OrderTextField(
                    value = viewState.customerPhone,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }.take(11)
                        onAction(CreateOrderScreenActions.OnCustomerPhoneChanged(filtered))
                    },
                    label = stringResource(R.string.phone_number),
                    error = viewState.customerPhoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    minLines = 1,
                    maxLines = 1,
                    modifier = Modifier.focusRequester(phoneFocusRequester)
                )

                OrderTextField(
                    value = viewState.customerAddress,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCustomerAddressChanged(it)) },
                    label = stringResource(R.string.delivery_address),
                    error = viewState.customerAddressError,
                    minLines = 2,
                    modifier = Modifier.focusRequester(addressFocusRequester)
                )

                OrderTextField(
                    value = viewState.collectionAmount,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                            .take(7)
                        onAction(CreateOrderScreenActions.OnCollectionAmountChanged(filtered))
                    },
                    label = stringResource(R.string.collection_amount),
                    error = viewState.collectionAmountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    minLines = 1,
                    maxLines = 1,
                    modifier = Modifier.focusRequester(collectionAmountFocusRequester)
                )

                OrderTextField(
                    value = viewState.deliveryFees,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                            .take(7)
                        onAction(CreateOrderScreenActions.OnDeliveryFeesChanged(filtered))
                    },                    label = stringResource(R.string.delivery_fees),
                    error = viewState.deliveryFeesError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    minLines = 1,
                    maxLines = 1,
                    modifier = Modifier.focusRequester(deliveryFeesFocusRequester)
                )

                // --- Add Note Field (not required, max 4 lines) ---
                OrderTextField(
                    value = viewState.note,
                    onValueChange = {
                        if (it.lines().size <= 4) {
                            onAction(CreateOrderScreenActions.OnNoteChanged(it))
                        }
                    },
                    label = stringResource(R.string.notes),
                    error = null,
                    minLines = 1,
                    maxLines = 4,
                )
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
                        style = mdSemiBold, // Button text style
                        color = Color.White
                    )
                }
            }
        }
    }

    if (viewState.showConfirmDialog) {
        BaseDialog(
            icon = painterResource(R.drawable.ic_confirm),
            title = stringResource(R.string.confirm_order_title),
            message = stringResource(R.string.confirm_order_message),
            dismissOnClickOutside = true, // Prevent accidental dismiss
            primaryButtonText = stringResource(R.string.confirm),
            secondaryButtonText = stringResource(R.string.dismiss),
            onPrimaryClick = {
                onAction(CreateOrderScreenActions.OnConfirmSubmit)
            },
            onSecondaryClick = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) },
            onDismiss = {
                onAction(CreateOrderScreenActions.OnDismissConfirmDialog)
            }

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
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = smMedium) }, // Field label style
        isError = error != null,
        supportingText = error?.let {
            {
                Text(
                    it, color = MaterialTheme.colorScheme.error, style = xsMedium // Error/supporting text style
                )
            }
        },
        keyboardOptions = keyboardOptions,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
        textStyle = mdMedium, // Field text style
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
            customerPhone = "1234567890",
            customerAddress = "123 Main St, City",
            collectionAmount = "250.00",
            deliveryFees = "20.00",
            deliveryTiming = DeliveryTiming.NOW
        ), onAction = {})
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun PreviewCreateOrderContentLater() {
    CreateOrderContent(
        viewState = CreateOrderViewState(
            customerPhone = "1234567890",
            customerAddress = "123 Main St, City",
            collectionAmount = "250.00",
            deliveryFees = "20.00",
            deliveryTiming = DeliveryTiming.LATER,
            scheduledDate = "25/12/2024",
            scheduledTime = "14:30"
        ), onAction = {})
}

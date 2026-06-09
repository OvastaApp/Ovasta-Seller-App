package com.ovasta.sellers.ui.screens.createorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.collection_amount
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.confirm_order_message
import com.ovasta.sellers.shared.resources.confirm_order_title
import com.ovasta.sellers.shared.resources.create_order
import com.ovasta.sellers.shared.resources.delivery_address
import com.ovasta.sellers.shared.resources.delivery_fees
import com.ovasta.sellers.shared.resources.dismiss
import com.ovasta.sellers.shared.resources.ic_confirm
import com.ovasta.sellers.shared.resources.notes
import com.ovasta.sellers.shared.resources.order_details
import com.ovasta.sellers.shared.resources.phone_number
import com.ovasta.sellers.ui.base.BaseScreen
import com.ovasta.sellers.ui.base.LocalNavigator
import com.ovasta.sellers.ui.components.BaseDialog
import com.ovasta.sellers.ui.components.CenteredTextAppBar
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdMedium
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.xsMedium
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val BrandColor = Color(0xFF006D98)

@Composable
fun CreateOrderScreen(viewModel: CreateOrderViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.onAction(CreateOrderScreenActions.ResetState)
    }

    BaseScreen(viewModel = viewModel) {
        CreateOrderContent(
            viewState = viewState,
            onAction = viewModel::onAction,
            onNavigateBack = { navigator.pop() }
        )
    }
}

@Composable
private fun CreateOrderContent(
    viewState: CreateOrderViewState,
    onAction: (CreateOrderScreenActions) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
                CenteredTextAppBar(
                    stringResource(Res.string.order_details),
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
                OrderTextField(
                    value = viewState.customerPhone,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }.take(11)
                        onAction(CreateOrderScreenActions.OnCustomerPhoneChanged(filtered))
                    },
                    label = stringResource(Res.string.phone_number),
                    error = viewState.customerPhoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    maxLines = 1
                )

                OrderTextField(
                    value = viewState.customerAddress,
                    onValueChange = { onAction(CreateOrderScreenActions.OnCustomerAddressChanged(it)) },
                    label = stringResource(Res.string.delivery_address),
                    error = viewState.customerAddressError,
                    minLines = 2
                )

                OrderTextField(
                    value = viewState.collectionAmount,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }.take(7)
                        onAction(CreateOrderScreenActions.OnCollectionAmountChanged(filtered))
                    },
                    label = stringResource(Res.string.collection_amount),
                    error = viewState.collectionAmountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    maxLines = 1
                )

                OrderTextField(
                    value = viewState.deliveryFees,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }.take(7)
                        onAction(CreateOrderScreenActions.OnDeliveryFeesChanged(filtered))
                    },
                    label = stringResource(Res.string.delivery_fees),
                    error = viewState.deliveryFeesError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    maxLines = 1
                )

                OrderTextField(
                    value = viewState.note,
                    onValueChange = {
                        if (it.lines().size <= 4) {
                            onAction(CreateOrderScreenActions.OnNoteChanged(it))
                        }
                    },
                    label = stringResource(Res.string.notes),
                    error = null,
                    minLines = 1,
                    maxLines = 4,
                )
            }

            // Submit Button
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
                        text = stringResource(Res.string.create_order),
                        style = mdSemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (viewState.showConfirmDialog) {
        BaseDialog(
            icon = painterResource(Res.drawable.ic_confirm),
            title = stringResource(Res.string.confirm_order_title),
            message = stringResource(Res.string.confirm_order_message),
            dismissOnClickOutside = true,
            primaryButtonText = stringResource(Res.string.confirm),
            secondaryButtonText = stringResource(Res.string.dismiss),
            onPrimaryClick = { onAction(CreateOrderScreenActions.OnConfirmSubmit) },
            onSecondaryClick = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) },
            onDismiss = { onAction(CreateOrderScreenActions.OnDismissConfirmDialog) }
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
        label = { Text(label, style = smMedium) },
        isError = error != null,
        supportingText = error?.let {
            { Text(it, color = MaterialTheme.colorScheme.error, style = xsMedium) }
        },
        keyboardOptions = keyboardOptions,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier.fillMaxWidth(),
        textStyle = mdMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandColor,
            focusedLabelColor = BrandColor,
            cursorColor = BrandColor
        )
    )
}

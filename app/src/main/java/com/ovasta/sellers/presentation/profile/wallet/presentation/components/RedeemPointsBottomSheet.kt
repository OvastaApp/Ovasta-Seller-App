package com.ovasta.sellers.presentation.profile.wallet.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.base.xsMedium
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletAction
import com.ovasta.sellers.presentation.profile.wallet.presentation.WalletViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemPointsBottomSheet(
    viewState: WalletViewState,
    onAction: (WalletAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(WalletAction.DismissRedeemBottomSheet) },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.convert_to_money),
                style = mdSemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${stringResource(R.string.mini_redeem_message)} ${viewState.minimumRedeemPoints.toInt()}",
                style = xsMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewState.redeemPointsInput,
                onValueChange = { onAction(WalletAction.UpdateRedeemPoints(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.points)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewState.redeemPointsError != null,
                supportingText = viewState.redeemPointsError?.let {
                    { Text(text = it, color = Color.Red) }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val inputValue = viewState.redeemPointsInput.toIntOrNull()
            val min = viewState.minimumRedeemPoints.toInt()
            val isButtonEnabled = inputValue != null && inputValue >= min && inputValue % min == 0

            Button(
                onClick = { onAction(WalletAction.ConfirmRedeemPoints) },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    style = smMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

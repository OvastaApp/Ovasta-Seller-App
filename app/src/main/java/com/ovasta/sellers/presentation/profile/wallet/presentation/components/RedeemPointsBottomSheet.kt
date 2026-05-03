package com.ovasta.sellers.presentation.profile.wallet.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.R
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smMedium
import com.ovasta.sellers.base.smSemiBold
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

    val min = viewState.minimumRedeemPoints.toInt()
    val availablePoints = (viewState.wallet?.points ?: 0.0).toInt()
    val steps = if (min > 0) availablePoints / min else 0
    val currentValue = viewState.redeemPointsInput.toIntOrNull() ?: 0

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
                text = "${stringResource(R.string.mini_redeem_message)} $min",
                style = xsMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selected points display
            Text(
                text = "$currentValue ${stringResource(R.string.the_points)}",
                style = smSemiBold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (steps > 0) {
                Slider(
                    value = currentValue.toFloat(),
                    onValueChange = { newValue ->
                        val snapped = (Math.round(newValue / min) * min).coerceIn(0, steps * min)
                        onAction(WalletAction.UpdateRedeemPoints(snapped.toString()))
                    },
                    valueRange = 0f..(steps * min).toFloat(),
                    steps = steps - 1,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = Primary.copy(alpha = 0.2f),
                        activeTickColor = Primary,
                        inactiveTickColor = Primary.copy(alpha = 0.4f)
                    )
                )

                // Labels row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0..steps) {
                        Text(
                            text = "${i * min}",
                            style = xsMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAction(WalletAction.ConfirmRedeemPoints) },
                enabled = currentValue > 0,
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

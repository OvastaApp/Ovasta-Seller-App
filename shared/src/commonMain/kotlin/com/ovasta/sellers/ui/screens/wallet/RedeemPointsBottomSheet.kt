package com.ovasta.sellers.ui.screens.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.confirm
import com.ovasta.sellers.shared.resources.convert_to_money
import com.ovasta.sellers.shared.resources.mini_redeem_message
import com.ovasta.sellers.shared.resources.point
import com.ovasta.sellers.ui.theme.Primary
import com.ovasta.sellers.ui.theme.mdSemiBold
import com.ovasta.sellers.ui.theme.smMedium
import com.ovasta.sellers.ui.theme.smSemiBold
import com.ovasta.sellers.ui.theme.xsMedium
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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

    println("DEBUG RedeemPointsBottomSheet: min=$min, availablePoints=$availablePoints, steps=$steps, currentValue=$currentValue")

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
                text = stringResource(Res.string.convert_to_money),
                style = mdSemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.mini_redeem_message, min.toString()),
                style = xsMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$currentValue ${stringResource(Res.string.point)}",
                style = smSemiBold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (steps > 0) {
                Slider(
                    value = currentValue.toFloat(),
                    onValueChange = { newValue ->
                        val snapped = ((newValue / min).roundToInt() * min).coerceIn(0, steps * min)
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
                    text = stringResource(Res.string.confirm),
                    style = smMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun RedeemPointsBottomSheetPreview() {
    val previewState = WalletViewState(
        wallet = com.ovasta.sellers.domain.model.WalletResponse(
            points = 200.0,
            walletBalance = 50.0,
            pointsHistory = emptyList()
        ),
        minimumRedeemPoints = 50.0,
        redeemPointsInput = "100",
        showRedeemBottomSheet = true
    )
    
    RedeemPointsBottomSheet(
        viewState = previewState,
        onAction = {}
    )
}

@Preview
@Composable
fun RedeemPointsBottomSheetPreviewEmpty() {
    val previewState = WalletViewState(
        wallet = com.ovasta.sellers.domain.model.WalletResponse(
            points = 200.0,
            walletBalance = 50.0,
            pointsHistory = emptyList()
        ),
        minimumRedeemPoints = 50.0,
        redeemPointsInput = "",
        showRedeemBottomSheet = true
    )
    
    RedeemPointsBottomSheet(
        viewState = previewState,
        onAction = {}
    )
}
package com.ovasta.sellers.presentation.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.smNormal

@Composable
fun TrackingToggleCard(
    isTracking: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor = if (isTracking) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val accentColor = if (isTracking) Color(0xFF2E7D32) else Color(0xFFB71C1C)

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTracking) ""
                    else "",
                    style = smNormal.copy(color = accentColor)
                )
            }

            androidx.compose.material3.Switch(
                checked = isTracking,
                onCheckedChange = { onToggle() },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD),
                    uncheckedBorderColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Base_white
import com.ovasta.sellers.base.Gray300
import com.ovasta.sellers.base.Gray700
import com.ovasta.sellers.base.smMedium

@Composable
fun NavigationAction(
    clickedTaskId: Int,
    onDirectionClick: () -> Unit,
    onContactClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(
            modifier = Modifier
                .weight(1f)
                .testTag("directionsButton_${clickedTaskId}"),
            text = "",
            onClick = onDirectionClick
        )
        ActionButton(
            modifier = Modifier
                .weight(1f)
                .testTag("contactButton_${clickedTaskId}"),
            text = "",
            onClick = onContactClick
        )
        ActionButton(
            modifier = Modifier
                .weight(1f)
                .testTag("contactButton_${clickedTaskId}"),
            text = "",
            onClick = onWhatsAppClick
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = Base_white,
        border = BorderStroke(1.dp, Gray300),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 10.dp
            ),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = smMedium.copy(color = Gray700),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStartedTaskNavigation() {
    NavigationAction(3, {}, {}, {})
}

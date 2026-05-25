package com.ovasta.sellers.base.components.sharedComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ovasta.sellers.base.Gray800
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.White
import com.ovasta.sellers.base.mdSemiBold
import com.ovasta.sellers.base.smMedium

@Composable
fun BaseDialog(
    icon: ImageVector? = null,
    title: String,
    message: String,
    dismissOnClickOutside: Boolean = true,
    primaryButtonText: String? = null,
    onPrimaryClick: () -> Unit = {},
    secondaryButtonText: String? = null,
    onSecondaryClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = title,
                    style = mdSemiBold,
                    color = Gray800,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = smMedium,
                    color = Gray800,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (primaryButtonText != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onPrimaryClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(
                            text = primaryButtonText,
                            style = mdSemiBold,
                            color = White
                        )
                    }
                }

                if (secondaryButtonText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onSecondaryClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Primary
                        )
                    ) {
                        Text(
                            text = secondaryButtonText,
                            style = mdSemiBold,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

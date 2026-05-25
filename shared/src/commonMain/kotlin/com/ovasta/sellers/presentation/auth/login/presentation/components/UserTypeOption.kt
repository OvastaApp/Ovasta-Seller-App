package com.ovasta.sellers.presentation.auth.login.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ovasta.sellers.base.Base_white
import com.ovasta.sellers.base.Gray200
import com.ovasta.sellers.base.Gray800
import com.ovasta.sellers.base.Primary
import com.ovasta.sellers.base.smMedium

@Composable
fun UserTypeOption(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else Base_white)
            .border(
                width = 1.dp,
                color = if (isSelected) Primary else Gray200,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = smMedium.copy(
                color = if (isSelected) Primary else Gray800
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

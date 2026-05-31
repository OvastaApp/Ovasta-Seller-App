package com.ovasta.sellers.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.arrow_narrow_left
import com.ovasta.sellers.ui.theme.mdSemiBold
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTextAppBar(
    title: String,
    showBackButton: Boolean = true,
    onBackButtonPressed: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        title = {
            Text(
                text = title,
                style = mdSemiBold,
                color = Color.Black
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackButtonPressed) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_narrow_left),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        },
        actions = actions
    )
}

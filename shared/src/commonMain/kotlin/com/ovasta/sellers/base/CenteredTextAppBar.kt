package com.ovasta.sellers.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTextAppBar(
    title: String,
    showBackButton: Boolean = true,
    onBackButtonPressed: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = Modifier, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                    Text(
                        text = "<",
                        style = mdSemiBold,
                        color = Color.Black
                    )
                }
            }
        },
        actions = actions
    )
}
